package com.smaato.interview.server.service;

import com.smaato.interview.server.pojo.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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
        if (!endpoint.isBlank() && !endpoint.isEmpty()) {
            endpoint = endpoint.strip();
            ResponseEntity<String> response;
            if (isHttpPostFeatureEnabled) {
                logger.info("POST call enabled for endpoint param");
                response = restTemplate.postForEntity(endpoint, new User("neo", "software developer"), String.class);
            } else {
                logger.info("POST call not enabled for endpoint param");
                response = restTemplate.getForEntity(endpoint, String.class);
            }
            logger.info("endpoint response status code: " + response.getStatusCodeValue());
            return response.getStatusCodeValue();
        }
        logger.info("endpoint is empty, no http call");
        return HttpStatus.OK.value();
    }
}
