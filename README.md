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
1. http://localhost:1307/greetings/random/\<name\>
1. http://localhost:1307/greetings/basic/\<name\>
1. http://localhost:1307/greetings/cowboy/\<name\>
1. http://localhost:1307/greetings/butler/\<name\>