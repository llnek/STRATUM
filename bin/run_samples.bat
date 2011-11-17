@echo off

if "%JAVA_HOME%" == "" goto no_jre

set SAMPLES_HOME=..\samples

set TPCL=..\thirdparty\*
set DIST=..\dist\*
set LIB=..\lib\*

set CP=%DIST%;%LIB%;%TPCL%

%JAVA_HOME%\bin\java -classpath "%CP%" demo.SampleApp %1 %SAMPLES_HOME%
goto end


:no_jre
echo Please set JAVA_HOME
goto end



:end
