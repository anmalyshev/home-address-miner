package com.onefactor.testtask

import scala.collection.mutable

import org.apache.spark.rdd.RDD

class HomeAddressMinerClassic extends HomeAddressMiner{
    
  def mineHomeAddresses(inputData: RDD[String]) : RDD[String] = {
    // Filter empty lines and remove header
    val filteredData = filterInputData(inputData)
    
    // Aggregate data by device id
    val initialSet = mutable.TreeSet.empty[DateCoordinates]
    val addToSet = (s: mutable.TreeSet[DateCoordinates], v: DateCoordinates) => s += v
    val mergePartitionSets = (p1: mutable.TreeSet[DateCoordinates], p2: mutable.TreeSet[DateCoordinates]) => p1 ++= p2
    
    val perDeviceData = filteredData.map { line => {
      val tokens = line.split(",");
      (tokens(0), new DateCoordinates(tokens(1).toLong, tokens(2).toInt, tokens(3).toInt))
    }}.aggregateByKey(initialSet)(addToSet, mergePartitionSets)
    
    // Now find home coordinates for each device
    val SECONDS_IN_HOUR = 3600l
    val CLUSTERING_MAX_DISTANCE = 10
    val MIN_INACTIVITY_HOURS = 3
    val MAX_INACTIVITY_HOURS = 24
    val MAX_TEMP_HOME_POINTS = 5
    val homeCoordinatesData = perDeviceData.map{deviceData => 
      {
        // Assumption is that device owner sleeps at home. Let's assume long inactivity period as sleep time
        // and consider points before and after sleep to be home address candidates.
        // We'll cluster them and calculate weigh of each cluster so that we can limit the number of points we check later.
        var curWeight = 0
        var totalPoints = 0
        var tempHomePoints = mutable.ArrayBuffer.empty[WeightedPoint]
        var previousDc : DateCoordinates = null
        deviceData._2.foreach { dc =>
          if (previousDc != null) {
             val delta = dc.timestamp - previousDc.timestamp
             if ((delta > MIN_INACTIVITY_HOURS * SECONDS_IN_HOUR) 
             && (delta < MAX_INACTIVITY_HOURS * SECONDS_IN_HOUR)) {
               var found = false;
               tempHomePoints.foreach { x => 
                 if (x.distanceFrom(previousDc.coordinates) < CLUSTERING_MAX_DISTANCE) {
                   x.append(previousDc.coordinates)
                   found = true;
                 }
               }
               if (!found) {
                 tempHomePoints += new WeightedPoint(previousDc.coordinates.x, previousDc.coordinates.y)
               }
               found = false;
               tempHomePoints.foreach { x => 
                 if (x.distanceFrom(dc.coordinates) < CLUSTERING_MAX_DISTANCE) {
                   x.append(dc.coordinates)
                   found = true;
                 }
               }
               if (!found) {
                 tempHomePoints += new WeightedPoint(dc.coordinates.x, dc.coordinates.y)
               }
             }
          }
          previousDc = dc
          totalPoints += 1
        }
        val maxWeightedOrdering : Ordering[WeightedPoint] = Ordering.by { 
          wp => -wp.weight
        }
        // limit number of candidate points.
        tempHomePoints = tempHomePoints.sorted(maxWeightedOrdering)
        if (tempHomePoints.size > MAX_TEMP_HOME_POINTS) {
          tempHomePoints.remove(MAX_TEMP_HOME_POINTS, tempHomePoints.size - MAX_TEMP_HOME_POINTS)
        }
        // Now let's build cluster around our candidate points and choose the one with highest weight
        var homePointsClusters = mutable.ArrayBuffer.concat(tempHomePoints)
        deviceData._2.foreach { dc => 
          homePointsClusters.foreach { tempHomePoint => 
            if (tempHomePoint.distanceFrom(dc.coordinates) < CLUSTERING_MAX_DISTANCE) {
              tempHomePoint.append(dc.coordinates);
            }
          }
        }
        homePointsClusters = homePointsClusters.sorted(maxWeightedOrdering)
        if (homePointsClusters.size > 0) {
          (deviceData._1, homePointsClusters(0))
        } else {
          (deviceData._1, new Point(Int.MinValue, Int.MinValue))
        }
      }
    }
    
    return homeCoordinatesData.map{ pair => pair._1 + "," + pair._2.x + "," + pair._2.y }
  }
}