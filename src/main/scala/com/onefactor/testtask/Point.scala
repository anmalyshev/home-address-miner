package com.onefactor.testtask

class Point(xc: Int, yc: Int) extends Serializable {
   var x = xc
   var y = yc
   
   def squareDistanceFrom(that : Point) : Double = {
      Math.pow(this.x - that.x, 2) +  Math.pow(this.y - that.y, 2)
   }
   
   def distanceFrom(that : Point) : Double = {
      Math.sqrt(squareDistanceFrom(that))
   }
   
   override def toString() : String = {
     x + "," + y
   }
}