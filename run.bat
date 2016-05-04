@echo off
call mvn install
call ./properties.bat
set HDFS_FILESYSTEM=hdfs://%HDFS_HOST%:%HDFS_PORT%/
call hdfs dfs -fs %HDFS_FILESYSTEM% -rm -R %HDFS_WORK_DIRECTORY%
call hdfs dfs -fs %HDFS_FILESYSTEM% -mkdir -p %HDFS_WORK_DIRECTORY%
call hdfs dfs -fs %HDFS_FILESYSTEM% -put ./data.csv %HDFS_WORK_DIRECTORY%/data.csv
call mvn exec:exec -Ddata.file=%HDFS_FILESYSTEM%%HDFS_WORK_DIRECTORY%/data.csv -Dresults.file=%HDFS_FILESYSTEM%%HDFS_WORK_DIRECTORY%/results.csv
call hdfs dfs -fs %HDFS_FILESYSTEM% -getmerge %HDFS_WORK_DIRECTORY%/results.csv ./results.csv
