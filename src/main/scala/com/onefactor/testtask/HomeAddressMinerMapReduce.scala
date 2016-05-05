package com.onefactor.testtask

import scala.collection.mutable

import org.apache.spark.rdd.RDD
import java.time.LocalDateTime
import java.time.Instant
import java.time.ZoneOffset

class HomeAddressMinerMapReduce extends HomeAddressMiner {
    
  def mineHomeAddresses(inputData: RDD[String]) : RDD[String] = {
    // Filter empty lines and remove header
    val filteredData = filterInputData(inputData)
    
    // Associate weight with each point based on timestamp and reduce by (device_id, x, y)
    val weightedData = filteredData.map { line => {
      val tokens = line.split(",");
      val deviceId = tokens(0)
      val timestamp = tokens(1).toLong
      val datetime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp * 1000), ZoneOffset.of("+00:03"))
      val coordinates = new Point(tokens(2).toInt, tokens(3).toInt)
      var weight : Int = 0;
      if (datetime.getHour <= 6) {
        weight = 10;
      } else if (datetime.getHour <= 12) {
        weight = 3;
      } else if (datetime.getHour <= 16) {
        weight = 1;
      } else if (datetime.getHour <= 20) {
        weight = 3;
      } else {
        weight = 10;
      }
      ((deviceId, coordinates), weight)
    }}.reduceByKey((w1,w2) => w1 + w2)
    
    // Make device_id the key and reduce to filter only max weighted point.
    weightedData.map((entry) => (entry._1._1, (entry._1._2, entry._2))).
    reduceByKey((entry1, entry2) => if (entry1._2 > entry2._2) entry1 else entry2).
    map(entry => entry._1 + "," + entry._2._1.x + "," + entry._2._1.y)
  }
}