# API Request Logger Service

## Features
- Request Logger service logs the number of unique request to log file or kafka(if `enabled` in  `application.yml` file).
- Currently API `/api/smaato/accept` is being logged.
- If `feature.http-post.enabled` is true, it will make POST call to given endpoint in query param.
- If `kafka.enabled` is `true`, request count will be send to kafka stream insted of log file.

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
	- checkout the project `git clone https://github.com/neo0057/request-logger-service.git`
	- build the project `mvn clean install`
	- #### Use command line to run
		- `java -jar target/smaato-server-1.0.0.jar`
		- by default server runs on 8080 port
	- #### Use IntelliJ to run
		- setup run configuration and select run as Application, select `ServerApplication.java` and run the project
		- by default server runs on 8080 port
		- to run multiple instances, change these two environment variables in configuration `feature.jobs.enabled=false` `server.port=<new_port_number>`

### To run multiple server instances behind load balancer
- for primary instance (by default run on ```8080``` port and ```feature.jobs.enabled``` is enabled): ```java -jar target/smaato-server-1.0.0.jar```
- for other instances (`disable job feature and change port number for each instance`)
        ```
        java -jar target/smaato-server-1.0.0.jar --feature.jobs.enabled=false --server.port=<port_number>
        ```
### If Kafka is enabled - (By default Kafka is enabled and need Kafka server to run on local)
- you can check request count message using this command `/home/<user>/kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic requests --from-beginning`
### Enable/Disable feature
- Go to `src/main/resources/application.yml` file and enable/disable particular feature
- To enable/disable Kafka: `kafka.enabled` to `true/false`
- To enable/disable GET vs POST call in API `api/smaato/accept`: set `feature.http-post.enabled` to `true/false`
