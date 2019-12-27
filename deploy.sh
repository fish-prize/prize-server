#!/bin/bash
echo '开始执行mvn package ...'
mvn package -DskipTests
echo '执行 mvn package 命令完成.'

cd target
mv server-0.0.1-SNAPSHOT.jar server.tmp
ls -a

echo '拷贝 server.tmp 到远程服务器'
scp server.tmp sweet_biancheng@47.104.220.91:/home/sweet_biancheng/server
echo '拷贝 server.tmp 到远程服务器 完成.'

echo '停止远程server.jar进程'
ssh sweet_biancheng@47.104.220.91 "sh /home/sweet_biancheng/server/stop.sh"
echo '停止远程server.jar进程成功.'


echo '删除远程 server.jar'
ssh sweet_biancheng@47.104.220.91 "rm /home/sweet_biancheng/server/server.jar"
echo '删除远程 server.jar成功.'

echo '重命名 server.tmp -> server.jar'
ssh sweet_biancheng@47.104.220.91 "mv /home/sweet_biancheng/server/server.tmp /home/sweet_biancheng/server/server.jar"
echo '重命名 server.tmp -> server.jar 完成.'

echo '启动远程 server.jar进程'
ssh sweet_biancheng@47.104.220.91 "sh /home/sweet_biancheng/server/start.sh"
echo '启动远程 server.jar进程成功.'
