package com.onefactor.testtask

import java.time.LocalDateTime
import java.time.Instant
import java.time.ZoneId


class DateCoordinates(ts: Long, x: Int, y: Int) extends Ordered[DateCoordinates] with Serializable {
  val timestamp = ts;
  val coordinates = new Point(x, y)
  
  override def toString() : String = {
    timestamp + "," + coordinates
  }
  
  def compare(that: DateCoordinates): Int = this.timestamp compare that.timestamp
  
}