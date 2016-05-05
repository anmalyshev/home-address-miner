package com.onefactor.testtask

import org.apache.spark.rdd.RDD

trait HomeAddressMiner {
  def mineHomeAddresses(inputData: RDD[String]) : RDD[String]
  
  def filterInputData(inputData: RDD[String]) : RDD[String] = {
    val fisrtLine = inputData.take(1)(0);
    val containsHeader = !fisrtLine.forall { x => Character.isDigit(x) || x == ','}
    inputData.filter(line => {!line.isEmpty() && (!containsHeader || !line.equals(fisrtLine))})
  }
  
}