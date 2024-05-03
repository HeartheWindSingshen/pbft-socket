@echo off

REM 设置需要运行的 Java 包的路径和参数
set PACKAGE3_PATH=C:\Users\AAA\Desktop\pbft-socket-web-run\3stmerge-0.0.1-SNAPSHOT.jar


REM 运行第四个 Java 包
echo changeto Utf-8
chcp 65001
echo Running Package 3...
java -jar "%PACKAGE3_PATH%"

pause
