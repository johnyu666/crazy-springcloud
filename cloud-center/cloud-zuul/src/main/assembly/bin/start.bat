echo off
title ZuulServerApplication

set SERVER_PORT=7799
set JAR_NAME=cloud-zuul-1.0-SNAPSHOT.jar
set MAIN_CLASS=com.crazymaker.springcloud.cloud.center.zuul.ZuulServerApplication
set JVM= -server -Xms1g -Xmx1g

set APPLICATION_CONFIG= -Dserver.port=%SERVER_PORT%


set DEBUG_OPTS=
if ""%1"" == ""debug"" (
   set DEBUG_OPTS= -Xdebug -Xrunjdwp:transport=dt_socket,address=5005,server=y,suspend=n
   goto debug
)

echo "Starting the %JAR_NAME%"
java %JVM% %APPLICATION_CONFIG%  -jar ../lib/%JAR_NAME% %MAIN_CLASS%
goto end

:debug
echo "start debug mode ......"
java %JVM% %APPLICATION_CONFIG%  %DEBUG_OPTS%  -jar ../lib/%JAR_NAME% %MAIN_CLASS%
goto end

:end
pause