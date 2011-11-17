@echo off

if "%JAVA_HOME%" == "" goto no_jre

if "%4" == "" goto usage
if "%3" == "" goto usage
if "%2" == "" goto usage
if "%1" == "" goto usage

set DIST=..\dist
set LIB=..\lib


set JARS=%LIB%\log4j-1.2.15.jar;%LIB%\slf4j-api-1.5.10.jar;%LIB%\slf4j-log4j12-1.5.10.jar;%LIB%\commons-logging-1.1.1.jar
set JARS=%JARS%;%LIB%\hsqldb.jar;%LIB%\zotoh-fxcore-1.2.0.jar


%JAVA_HOME%\bin\java -classpath "%JARS%" com.cherimoia.core.db.HsqlDBSQL -url:jdbc:hsqldb:file:%1 -user:%2 -password:%3 -sql:%4
goto end


:usage
echo.
echo.
echo sql_hsqldb {db-url} {user} {password} {sql-file}
echo e.g.
echo.
echo sql_hsqldb /home/user1/db/dbf user1 secret /home/user1/ddl.sql

goto end


:no_jre
echo Please set JAVA_HOME
goto end


:end
