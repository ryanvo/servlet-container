#!/bin/sh

ant build &
sleep 2

cd target/WEB-INF 

java -classpath lib/*:classes/. edu.upenn.cis455.webserver.HttpServer 8080 /home/rtv/Workspace/servlet-container/www /home/rtv/Workspace/servlet-container/conf/web.xml 20 1000 &
sleep 2

bash ../../test.sh

curl http://localhost:8080/shutdown