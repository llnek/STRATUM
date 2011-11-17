#! /bin/sh

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
	echo "start_hsqldb_server <db-file> <db-id> <port>"
	echo "e.g."
	echo ""
	echo "start_hsqldb_server /home/user1/dbdir/db mydb 9001"
	echo ""
	echo ""
	exit 2
fi

LIB=../lib
JARS=${LIB}/hsqldb.jar

${JAVA_HOME}/bin/java -classpath "${JARS}" org.hsqldb.Server -port $3 -database.0 file:$1 -dbname.0 $2 -silent false -trace true
exit 0


