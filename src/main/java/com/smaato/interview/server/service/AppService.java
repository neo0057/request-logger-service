package com.smaato.interview.server.service;

import com.smaato.interview.server.pojo.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AppService {

    static final Logger logger = LoggerFactory.getLogger(AppService.class);

    @Autowired
    RestTemplate restTemplate;

    @Value("${feature.http-post.enabled}")
    Boolean isHttpPostFeatureEnabled;

    public int callEndpoint(String endpoint) {
        ResponseEntity<String> response = null;
        if (endpoint != null && endpoint.strip().length() > 0) {
            endpoint = endpoint.strip();
            if (isHttpPostFeatureEnabled) {
                response = restTemplate.postForEntity(endpoint, new User("neo", "software developer"), String.class);
            } else {
                response = restTemplate.getForEntity(endpoint, String.class);
            }
            logger.info("endpoint response status code: " + response.getStatusCodeValue() + ", body: " + response.getBody());
        } else {
            logger.info("endpoint is empty");
        }
        return (isHttpPostFeatureEnabled && response != null) ? response.getStatusCodeValue() : 200;
    }
}
