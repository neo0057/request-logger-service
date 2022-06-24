package com.smaato.interview.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AppService {

    static final Logger logger = LoggerFactory.getLogger(AppService.class);

    @Autowired
    RestTemplate restTemplate;

    @Async("taskExecutor")
    public void callEndpoint(String endpoint) {
        if (endpoint != null && endpoint.trim().length() > 0) {
            ResponseEntity<String> response = restTemplate.getForEntity(endpoint, String.class);
            logger.info("endpoint response status: " + response.getStatusCodeValue());
        } else {
            logger.info("endpoint is empty");
        }
    }
}
