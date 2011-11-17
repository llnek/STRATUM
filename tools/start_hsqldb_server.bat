@echo off

if "%JAVA_HOME%" == "" goto no_jre

if "%3" == "" goto usage
if "%2" == "" goto usage
if "%1" == "" goto usage

set LIB=..\lib

set JARS=%LIB%\hsqldb.jar
set PORT=9001

%JAVA_HOME%\bin\java -classpath %JARS% org.hsqldb.Server -port %3 -database.0 file:%1 -dbname.0 %2 -silent false -trace true
goto end


:usage
echo.
echo.
echo start_hsqldb_server {db-file} {db-id} {port}
echo e.g.
echo.
echo start_hsqldb_server /home/user1/dbdir/db mydb 9001
goto end


:no_jre
echo Please set JAVA_HOME
goto end


:end

