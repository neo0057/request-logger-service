# API Request Logger Service
[![Build Status](https://travis-ci.org/joemccann/dillinger.svg?branch=master)](https://github.com/neo0057/request-logger-service)

## Features

- Request Logger service logs the number of unique request to log file or kafka(if enabled from property yaml file).
- Currently API /api/smaato/accept is being logged.
- If feature.http-post.enabled is true, it will make POST call to given endpoint in param

## High Level Design

<img src=high-level-design.png/>

### How to run
1. #### software/dependency required
        1. JDK 17 version
        2. Maven 3.8.6 version
        3. Redis cache server running on localhost:6379 port
        4. Apache kafka server running on localhost:9092 port
        5. Git
2. #### How to Run
    ```sh
    1. git clone https://github.com/neo0057/request-logger-service.git
    2. mvn clean install
    3. java -jar target/smaato-server-1.0.0.jar
    ```

### To run multiple server instances behind load balancer
- for primary instance (by default run on 8080 port and jobs feature is enabled): ```java -jar target/smaato-server-1.0.0.jar```
- for other instances (disable job feature and change port number for each instance)
        ```
        java -jar target/smaato-server-1.0.0.jar --feature.jobs.enabled=false --server.port=<port_number>
        ```
### If kafka is enabled
- you can check message using this command ```/home/<user>/kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic requests --from-beginning```
```