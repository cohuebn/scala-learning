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
There is an auxillary project called system that can be used to setup external systems (e.g. Kafka)

To install system components take the following steps:
1. open a console window capable of running sbt
1. navigate to the _cory-learns-scala_ root directory
1. run ```sbt install```

To run Kafka, navigate to system's target directory and run one of the following from a shell:
* Mac/Linux - run.sh
* Windows - run.ps1
When finished, press any key in the console to stop the Zookeeper and Kafka servers