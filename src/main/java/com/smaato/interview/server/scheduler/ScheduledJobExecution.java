package com.smaato.interview.server.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class ScheduledJobExecution {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledJobExecution.class);

    @Bean
    @ConditionalOnProperty(value = "jobs.enabled", havingValue = "true")
    public ScheduledJob scheduledJob() {
        logger.info("job scheduled");
        return new ScheduledJob();
    }
}
