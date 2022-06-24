package com.smaato.interview.server.logger;

import com.smaato.interview.server.entity.Request;
import com.smaato.interview.server.repository.RequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class RequestLogger {

    private static final Logger logger = LoggerFactory.getLogger(RequestLogger.class);

    @Autowired
    RequestRepository requestRepository;

    @Async
    public void logRequestForId(Integer id) {
        LocalDateTime now = LocalDateTime.now();
        requestRepository.save(new Request(id, now.getHour(), now.getMinute()));
        logger.info("request saved in redis cache");
    }
}
