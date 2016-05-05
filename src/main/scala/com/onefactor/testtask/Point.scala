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
   
  def canEqual(a: Any) = a.isInstanceOf[Point]

  override def equals(that: Any): Boolean =
    that match {
      case that: Point => that.canEqual(this) && this.x == that.x && this.y == that.y
      case _ => false
   }

  override def hashCode:Int = {
    val prime = 31
    var result = 1
    result = prime * result + x
    result = prime * result + y
    return result
  }
}