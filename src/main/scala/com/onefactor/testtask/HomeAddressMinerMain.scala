package com.onefactor.testtask

import scala.collection.mutable

import org.apache.spark.SparkContext
import org.apache.spark.SparkConf

object HomeAddressMinerMain {
  
  def main(args : Array[String]) {
    if (args.length < 2) {
      println("Arguments missing.\n")
      printUsage();
      return 
    }
    
    val conf = new SparkConf().setAppName("HomeAddressMiner").setMaster("local")
    val sc = new SparkContext(conf)
    val miner = new HomeAddressMiner();
    
    val homeAddresses = miner.mineHomeAddresses(sc.textFile(args(0)));
    homeAddresses.sortBy(f =>f.split(",")(0).toInt, true, 1).saveAsTextFile(args(1))
  }
  
  def printUsage() {
    println("2 arguments are required:")
    println("   Data file URL: hdfs://localhost:9000/data.csv")
    println("   Result file URL: hdfs://localhost:9000/results.csv")
  }

}
