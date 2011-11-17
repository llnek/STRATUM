#!/bin/sh

if [ "$JAVA_HOME" = "" ] ; then
	echo "Please set JAVA_HOME"
	exit 1
fi


SAMPLES_HOME=../samples

TPCL=../thirdparty/*
DIST=../dist/*
LIB=../lib/*

CP=${DIST}:${LIB}:${TPCL}

${JAVA_HOME}/bin/java -classpath "${CP}" demo.SampleApp $1 ${SAMPLES_HOME}
exit 0
