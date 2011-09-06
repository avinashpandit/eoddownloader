cd "C:\MyPrograms\JSystemTrader-6.24"

echo net start mysql

net start mysql

set JAVA_HOME=%jdk16%

call GoogleDownloader.bat

call BulkStrategyRunner.bat

call ant clean 

call startServer.bat
