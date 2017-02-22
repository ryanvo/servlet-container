#!/bin/sh

ant build &
sleep 2

cd target/WEB-INF 

java -classpath lib/*:classes/. HttpServer 8080 /Users/ryan/Workspace/servlet-container/www /Users/ryan/Workspace/servlet-container/conf/web.xml 20 1000 &
sleep 2

bash ../../test.sh

curl http://localhost:8080/shutdown
