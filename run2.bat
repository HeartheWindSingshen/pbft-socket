@echo off

REM 设置需要运行的 Java 包的路径和参数
set PACKAGE2_PATH=C:\Users\AAA\Desktop\pbft-socket-web-run\2stmerge-0.0.1-SNAPSHOT.jar


REM 运行第三个 Java 包
echo changeto Utf-8
chcp 65001
echo Running Package 2...
java -jar "%PACKAGE2_PATH%"

pause
