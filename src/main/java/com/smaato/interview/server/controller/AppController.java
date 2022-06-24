package com.smaato.interview.server.controller;

import com.smaato.interview.server.logger.RequestLogger;
import com.smaato.interview.server.service.AppService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/smaato")
public class AppController {

    static final Logger logger = LoggerFactory.getLogger(AppController.class);

    @Autowired
    AppService appService;

    @Autowired
    RequestLogger requestLogger;

    @GetMapping("/accept")
    public String get(@RequestParam(value = "id") Integer id, @RequestParam(value = "endpoint", required = false) String endpoint) {
        appService.callEndpoint(endpoint);
        requestLogger.logRequestForId(id);
        return "ok";
    }
}
