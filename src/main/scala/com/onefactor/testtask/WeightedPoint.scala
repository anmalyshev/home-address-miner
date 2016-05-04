package com.onefactor.testtask

class WeightedPoint(xc: Int, yc: Int) extends Point (xc, yc) {
   var weight = 1
   
   def append(that : Point) = {
      x = (x * weight + that.x) / (weight + 1)
      y = (y * weight + that.y) / (weight + 1)
      weight += 1
   }
   
   override def toString() : String = {
     super.toString()+ "," + weight
   }
}