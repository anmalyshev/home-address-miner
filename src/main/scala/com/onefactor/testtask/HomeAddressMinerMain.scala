package com.onefactor.testtask

import scala.collection.mutable

import org.apache.spark.SparkContext
import org.apache.spark.SparkConf

object HomeAddressMinerMain {
  
  def main(args : Array[String]) {
    if (args.length < 3) {
      println("Arguments missing.\n")
      printUsage();
      return 
    }
    
    val conf = new SparkConf().setAppName("HomeAddressMiner").setMaster("local")
    val sc = new SparkContext(conf)
    var miner : HomeAddressMiner = null
    args(2) match {
      case "classic" => miner = new HomeAddressMinerClassic();
      case "mapreduce" => miner = new HomeAddressMinerMapReduce();
      case _ => { printUsage(); return}
    }
    
    val homeAddresses = miner.mineHomeAddresses(sc.textFile(args(0)));
    homeAddresses.sortBy(f =>f.split(",")(0).toInt, true, 1).saveAsTextFile(args(1))
  }
  
  def printUsage() {
    println("3 arguments are required:")
    println("   Data file URL: hdfs://localhost:9000/data.csv")
    println("   Result file URL: hdfs://localhost:9000/results.csv")
    println("   Algorithm: classic or mapreduce")
  }

}
