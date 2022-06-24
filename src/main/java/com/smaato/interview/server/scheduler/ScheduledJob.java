package com.smaato.interview.server.scheduler;

import com.smaato.interview.server.entity.Request;
import com.smaato.interview.server.repository.RequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class ScheduledJob {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledJob.class);

    @Value("${log.file.full-path}")
    String filePath;

    @Autowired
    RequestRepository requestRepository;

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
        int hour = now.getHour();
        int minute = now.getMinute();
        if (minute == 0) {
            minute = 59;
            hour--;
        } else minute--;
        final int finalHour = hour;
        final int finalMinute = minute;
        long start = System.currentTimeMillis();
        Iterable<Request> requestIterable = requestRepository.findAll();
        long end = System.currentTimeMillis();
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
        boolean noData = requestCountMap.size() == 0;
        logger.info("time to fetch " + requestList.size() + " records from redis hashSet: " + (end - start) + " ms.");
        StringBuilder fileContentToAdd = new StringBuilder();
        if (fileCreated) fileContentToAdd.append("hour:min").append("\t").append("request-count");
        for (String mapKey : requestCountMap.keySet()) {
            fileContentToAdd.append(System.lineSeparator()).append(mapKey).append("\t\t").append(requestCountMap.get(mapKey));
        }
        String key = getFormatted(finalHour) + ":" + getFormatted(finalMinute);
        if (noData) fileContentToAdd.append(System.lineSeparator()).append(key).append("\t\t").append(0);
        requestRepository.deleteAll(requestList);
        FileWriter fileWriter = new FileWriter(logFile, true);
        BufferedWriter bufferWriter = new BufferedWriter(fileWriter);
        bufferWriter.write(fileContentToAdd.toString());
        bufferWriter.close();
        fileWriter.close();
        logger.info("request count added to log for time: " + key);
    }

    private String getFormatted(Integer n) {
        if (n > 9) return String.valueOf(n);
        return "0" + n;
    }
}