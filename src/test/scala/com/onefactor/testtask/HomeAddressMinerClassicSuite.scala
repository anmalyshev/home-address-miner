package com.onefactor.testtask

import scala.io.Source

import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import org.junit.runner.RunWith

import com.holdenkarau.spark.testing._

@RunWith(classOf[JUnitRunner])
class HomeAddressMinerClassicSuite extends FunSuite with SharedSparkContext {
  test("device owner should sleep") {
    val input = Source.fromInputStream(getClass.getResourceAsStream("/com/onefactor/testtask/nosleep.csv")).getLines.toList
    val expected = Array("1," + Int.MinValue + "," + Int.MinValue)
    val miner = new HomeAddressMinerClassic();
    val results = miner.mineHomeAddresses(sc.parallelize(input,4)).collect();
    assert(results.deep == expected.deep)
  }
  
  test("device owner sleeps, one candidate home point") {
    val input = Source.fromInputStream(getClass.getResourceAsStream("/com/onefactor/testtask/sleeponepoint.csv")).getLines.toList
    val expected = Array("1,200,200")
    val miner = new HomeAddressMinerClassic();
    val results = miner.mineHomeAddresses(sc.parallelize(input,4)).collect();
    assert(results.deep == expected.deep)
  }
  
  test("device owner sleeps, more than one candidate home point") {
    val input = Source.fromInputStream(getClass.getResourceAsStream("/com/onefactor/testtask/sleepmorepoints.csv")).getLines.toList
    val expected = Array("1,200,200")
    val miner = new HomeAddressMinerClassic();
    val results = miner.mineHomeAddresses(sc.parallelize(input,4)).collect();
    assert(results.deep == expected.deep)
  }
  
  test("real data should be close to manual analisys") {
    val input = Source.fromInputStream(getClass.getResourceAsStream("/com/onefactor/testtask/realdata.csv")).getLines.toList
    val miner = new HomeAddressMinerClassic();
    val expected = Map(("1070",new Point(2489,5693)),("778",new Point(2783,5693)))
    val results = miner.mineHomeAddresses(sc.parallelize(input,4)).collect().map { x => 
      val tokens = x.split(",");
      (tokens(0),new Point(tokens(1).toInt, tokens(2).toInt))
    }
    results.foreach(result => {
      expected.get(result._1).get.distanceFrom(result._2) < 20
    })
  }
}
