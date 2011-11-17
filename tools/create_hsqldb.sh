#!/bin/sh

if [ "${JAVA_HOME}" = "" ] ; then
	echo "Please set JAVA_HOME"
	exit 1
fi

no_args=NO

if [ "$3" = "" ] ; then
no_args=YES
fi
if [ "$2" = "" ] ; then
no_args=YES
fi
if [ "$1" = "" ] ; then
no_args=YES
fi

if [ "$no_args" = "YES" ] ; then
	echo ""
	echo ""
	echo "create_hsqldb <db-dir> <user> <password> <sql-file>"
	echo "e.g."
	echo ""
	echo "create_hsqldb /home/user1/db user1 secret /home/user1/ddl.sql"
	echo ""
	echo ""
	exit 2
fi


DIST=../dist
LIB=../lib


JARS=${LIB}/log4j-1.2.15.jar:${LIB}/slf4j-api-1.5.10.jar:${LIB}/slf4j-log4j12-1.5.10.jar:${LIB}/commons-logging-1.1.1.jar
JARS=${JARS}:${LIB}/commons-dbcp-1.2.2.jar:${LIB}/commons-pool-1.4.jar:${LIB}/hsqldb.jar
JARS=${JARS}:${LIB}/zotoh-fxcore-1.2.0.jar:${DIST}/zotoh-simdb-1.1.0.jar


echo "Creating database, please wait..."
${JAVA_HOME}/bin/java -classpath "${JARS}" com.cherimoia.core.db.HsqlDBSQL -create:$1 -user:$2 -password:$3 -sql:$4
exit 0



