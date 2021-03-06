# Cory learns Scala

## Console app
To run the console app, take the following steps:
1. open a console window capable of running sbt
1. navigate to the _cory-learns-scala_ root directory
1. run ```sbt ~console/run```
If it worked, you should see greetings print to the console window

## Web API
To run the web API, take the following steps:
1. open a console window capable of running sbt
1. navigate to the _cory-learns-scala_ root directory
1. run ```sbt web/run```

If it worked, you should be able to hit the following:
1. http://localhost:1307/greetings/dialects/random/{{name}}
1. http://localhost:1307/greetings/dialects/basic/{{name}}
1. http://localhost:1307/greetings/dialects/cowboy/{{name}}
1. http://localhost:1307/greetings/dialects/butler/{{name}}
1. http://localhost:1307/greetings/dialects/millennial/{{name}}

## System
There is an auxiliary project called system that can be used to setup external systems (e.g. Kafka)

### Install Kafka
To install system components take the following steps:
1. open a console window capable of running sbt
1. navigate to the _cory-learns-scala_ root directory
1. run ```sbt install```

### Running Kafka
To run Kafka, navigate to system's target directory and run one of the following from a shell:
* Mac/Linux - run.sh
* Windows - run.ps1
When finished, press any key in the console to stop the Zookeeper and Kafka servers

### Populating data
Once Kafka is running you can send random strings to the test topic using:
1. open a console window capable of running sbt
1. navigate to the _cory-learns-scala_ root directory
1. run ```sbt system/sendKafkaData```

### Shell commands (assumes default ports):
* List all topics - ```system/target/kafka/bin/kafka-topics.sh --zookeeper localhost:2181 --list```
* Create a topic - ```system/target/kafka/bin/kafka-topics.sh -zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic greetings```
* Read all messages to a topic - ```system/target/kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic greetings --from-beginning```
* Delete a topic - ```system/target/kafka/bin/zookeeper-shell.sh localhost:2181 rmr /brokers/topics/greetings```
