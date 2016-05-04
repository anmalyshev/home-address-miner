#!/bin/sh
mvn clean install
source ./properties.sh
hdfs dfs -fs $HDFS_HOST:$HDFS_PORT -rm -R $HDFS_WORK_DIRECTORY
hdfs dfs -fs $HDFS_HOST:$HDFS_PORT -mkdir -p $HDFS_WORK_DIRECTORY
hdfs dfs -fs $HDFS_HOST:$HDFS_PORT -put ./data.csv $HDFS_WORK_DIRECTORY/data.csv
mvn exec:exec -Ddata.file=hdfs://$HDFS_HOST:$HDFS_PORT$HDFS_WORK_DIRECTORY/data.csv -Dresults.file=hdfs://$HDFS_HOST:$HDFS_PORT$HDFS_WORK_DIRECTORY/results.csv
hdfs dfs -fs $HDFS_HOST:$HDFS_PORT -getmerge $HDFS_WORK_DIRECTORY/results.csv ./results.csv