#!/bin/sh
mvn install
source ./properties.sh
HDFS_FILESYSTEM=hdfs://$HDFS_HOST:$HDFS_PORT/
hdfs dfs -fs $HDFS_FILESYSTEM -rm -R $HDFS_WORK_DIRECTORY
hdfs dfs -fs $HDFS_FILESYSTEM -mkdir -p $HDFS_WORK_DIRECTORY
hdfs dfs -fs $HDFS_FILESYSTEM -put ./data.csv $HDFS_WORK_DIRECTORY/data.csv
mvn exec:exec -Ddata.file=$HDFS_FILESYSTEM$HDFS_WORK_DIRECTORY/data.csv -Dresults.file=$HDFS_FILESYSTEM$HDFS_WORK_DIRECTORY/results_classic.csv -Dmining.algorithm=classic
hdfs dfs -fs $HDFS_FILESYSTEM -getmerge $HDFS_WORK_DIRECTORY/results_classic.csv ./results_classic.csv
mvn exec:exec -Ddata.file=$HDFS_FILESYSTEM$HDFS_WORK_DIRECTORY/data.csv -Dresults.file=$HDFS_FILESYSTEM$HDFS_WORK_DIRECTORY/results_mapreduce.csv -Dmining.algorithm=mapreduce
hdfs dfs -fs $HDFS_FILESYSTEM -getmerge $HDFS_WORK_DIRECTORY/results_mapreduce.csv ./results_mapreduce.csv