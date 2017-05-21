#!/bin/sh
source /etc/profile

usage()
{
    echo "Usage: ${0##*/} {start|stop|restart} [ CONFIGS ... ] "
    exit 1
}

[ $# -gt 0 ] || usage

SERVER_MAIN="com.sohu.bp.elite.server.EliteServer"
SERVER_NAME="elite-service"
SERVICE_HOME="/opt/service/"${SERVER_NAME}
ONLINE_ENV=" -Dconfig.product=false "
JVM_OPTIONS="-server -Xms2g -Xmx2g -Xmn512m -Xss250k -XX:SurvivorRatio=30 -XX:PermSize=256m -XX:MaxPermSize=256m -XX:+UseParNewGC -XX:MaxGCPauseMillis=50 -XX:+UseConcMarkSweepGC -XX:CMSInitiatingOccupancyFraction=70 -XX:+UseCMSCompactAtFullCollection -XX:CMSFullGCsBeforeCompaction=3 -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintGCApplicationConcurrentTime -XX:+PrintGCApplicationStoppedTime -Xloggc:/opt/logs/service/gc.log_"${SERVER_NAME}
HOST_IP=`ip a | grep eth0 | grep inet | awk '{print $2}' | awk -F '/' '{print $1}'`
JMX_OPTIONS='-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.port=10010 -Djava.rmi.server.hostname='${HOST_IP}

start()
{
    echo "${SERVER_NAME} is going to start ..."
    lib='.'
    for jar in `ls ${SERVICE_HOME}/lib/*.jar`
    do
    lib=$lib:$jar
    done
    nohup java -cp ${lib} ${JVM_OPTIONS} ${JMX_OPTIONS} ${ONLINE_ENV} ${SERVER_MAIN} 1>/dev/null 2>/dev/null &
    echo "${SERVER_NAME} is started ..."
}

status()
{
    netstat -ntpl | grep 13907 | awk '{print $7}'| awk -F "/" '{print $1}'|while read pid
    do
    echo "${SERVER_NAME} pid is $pid "
    ps -p $pid -olstart
    done
}

stop()
{
    echo "${SERVER_NAME} is going to stop ..."
    netstat -ntpl | grep 13907 | awk '{print $7}'| awk -F "/" '{print $1}'|while read pid
    do
        kill -9 $pid
    done
    echo  "${SERVER_NAME} is stoped ..."
}


case "$1" in
start)
  start  $2
  ;;
stop)
  stop
  ;;
status)
  status
  ;;
restart)
  stop
  sleep 3
  start   $2
  ;;
*)
  printf "Usage: ${0##*/} {start|stop|restart} [ CONFIGS ... ] "
  exit 1
  ;;
esac