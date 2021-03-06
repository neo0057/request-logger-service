package com.smaato.interview.server.controller;

import com.smaato.interview.server.logger.RequestLogger;
import com.smaato.interview.server.service.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/smaato")
public class AppController {

    @Autowired
    AppService appService;

    @Autowired
    RequestLogger requestLogger;

    @GetMapping("/accept")
    public String get(@RequestParam(value = "id") Integer id,
                      @RequestParam(value = "endpoint", required = false) String endpoint) {
        LocalDateTime now = LocalDateTime.now();
        int status = appService.callEndpoint(endpoint);
        requestLogger.logRequestForId(id, now);
        return HttpStatus.valueOf(status).is2xxSuccessful() ? "ok" : "failed";
    }
}
