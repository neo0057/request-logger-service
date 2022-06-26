package com.smaato.interview.server.scheduler;

import com.smaato.interview.server.entity.Request;
import com.smaato.interview.server.repository.RequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

public class ScheduledJob {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledJob.class);

    @Value("${log.file.full-path}")
    String filePath;

    @Autowired
    RequestRepository requestRepository;

    @Value("${kafka.enabled}")
    boolean isKafkaEnabled;

    @Value(value = "${kafka.topicName}")
    private String topicName;

    @Value("${kafka.group}")
    private String groupId;

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @Async
    @Scheduled(cron = "0 0/1 * * * ?")
    public void processRequestToLogFile() throws Exception {
        logger.info("scheduler call at " + LocalDateTime.now());
        File logFile = new File(filePath);
        boolean fileCreated = false;
        if (!logFile.exists()) {
            try {
                fileCreated = logFile.createNewFile();
            } catch (IOException ex) {
                logger.error("error while creating file: " + filePath + ", error: " + ex.getLocalizedMessage());
                throw new Exception(ex.getLocalizedMessage());
            }
            logger.info("New Log File Created..");
        }
        LocalDateTime now = LocalDateTime.now();
        final int hour = (now.getMinute() == 0) ? now.getHour() - 1 : now.getHour();
        final int minute = (now.getMinute() == 0) ? 59 : now.getMinute() - 1;
        Iterable<Request> requestIterable = requestRepository.findAll();
        List<Request> requestList = new ArrayList<>();
        Map<String, Boolean> requestMap = new HashMap<>();
        Map<String, Integer> requestCountMap = new TreeMap<>();
        for (Request request : requestIterable) {
            requestList.add(request); // to delete entry
            String uniqueKey = getFormatted(request.getHour()) + ":" + getFormatted(request.getMinute());
            String mapKey = getFormatted(request.getHour()) + ":" + getFormatted(request.getMinute()) + "-" + request.getRequestId();
            if (!requestMap.containsKey(mapKey)) {
                requestMap.put(mapKey, true);
                requestCountMap.put(uniqueKey, requestCountMap.getOrDefault(uniqueKey, 0) + 1);
            }
        }
        boolean noData = (requestCountMap.size() == 0);
        StringBuilder fileContentToAdd = new StringBuilder();
        if (fileCreated) fileContentToAdd.append("hour:min").append("\t").append("request-count");
        for (String mapKey : requestCountMap.keySet()) {
            fileContentToAdd.append(System.lineSeparator()).append(mapKey).append("\t\t").append(requestCountMap.get(mapKey));
        }
        String key = getFormatted(hour) + ":" + getFormatted(minute);
        if (noData) fileContentToAdd.append(System.lineSeparator()).append(key).append("\t\t").append(0);
        requestRepository.deleteAll(requestList);
        if (isKafkaEnabled) sendMessage(fileContentToAdd.toString());
        else addDataToFile(logFile, fileContentToAdd.toString());
        logger.info("request count added to log for time: " + key);
    }

    public void sendMessage(String message) {
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topicName, message);
        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onSuccess(SendResult<String, String> result) {
                System.out.println("Sent message=[" + message + "] with offset=[" + result.getRecordMetadata().offset() + "]");
            }

            @Override
            public void onFailure(Throwable ex) {
                System.out.println("Unable to send message=[" + message + "] due to : " + ex.getMessage());
            }
        });
    }

    private String getFormatted(Integer n) {
        if (n > 9) return String.valueOf(n);
        return "0" + n;
    }

    private void addDataToFile(File logFile, String data) throws IOException {
        FileWriter fileWriter = new FileWriter(logFile, true);
        BufferedWriter bufferWriter = new BufferedWriter(fileWriter);
        bufferWriter.write(data);
        bufferWriter.close();
        fileWriter.close();
    }

    @KafkaListener(topics = "${kafka.topicName}", groupId = "${kafka.group}")
    public void listenGroupFoo(String message) {
        System.out.println("Received Message in group " + groupId + " : " + message);
    }
}
