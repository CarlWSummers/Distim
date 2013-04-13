package edu.uccs.summers.data

import scala.collection.mutable
import scala.collection.mutable.IndexedSeq

class TerrainType(val x : Int, val y : Int, private[data] var _distance : Int){
  
  def distance = _distance
}

case class Open(a : Int, b : Int, var dist : Int) extends TerrainType(a,b , dist) {
  override def toString = "."
}

case class Wall(a : Int, b : Int) extends TerrainType(a, b, Int.MaxValue) {
  override def toString = "x"
}

case class Exit(a : Int, b : Int) extends TerrainType(a, b, 0) {
  override def toString = "e"
}

case class Null(a : Int, b : Int) extends TerrainType(a, b, Int.MaxValue) {
  override def toString = "_"
}

class Topography(file : String) {

  val source = io.Source.fromFile(file)
  val lines = source.getLines.toArray
  val width = lines.size;
  val height = lines(0).size;
  val grid = IndexedSeq.fill[TerrainType](width, height)(new Null(0,0))
  
  private val exitSet = mutable.Set[Exit]()
  
  for(i <- 0 to (width - 1);
      j <- 0 to (lines(i).size - 1)) yield lines(i).charAt(j) match {
    case 'x' => grid(i)(j) = new Wall(i,j)
    case '.' => grid(i)(j) = Open(i,j,Int.MaxValue)
    case 'e' => grid(i)(j) = {
      val exit = new Exit(i,j)
      exitSet += exit
      exit
    }
    case _ => 
  }
  source.close
  floodFill()
  
  def getType(x : Int, y : Int) : TerrainType = {
    if(x < grid.length){
      if(y < grid(x).length){
        return grid(x)(y)
      }
    }
    return new Null(x,y)
  }
  def distance(x :Int, y : Int) = grid(x)(y).distance
  def isOpen(x : Int, y : Int) = grid(x)(y).isInstanceOf[Open]
  def isExit(x : Int, y : Int) = grid(x)(y).isInstanceOf[Exit]
  def isNotBlocked(x : Int, y : Int) = {
    if(x < 0 || x >= grid.size || y < 0 || y >= grid(x).size) false
    else{
      val gridType = grid(x)(y)
      gridType.isInstanceOf[Exit] || gridType.isInstanceOf[Open]
    }
  }

  def maxDimension() : Int = (grid.length /: grid)((currentMax, subArray) => currentMax max subArray.length)

  private def floodFill(){
    val open = mutable.Queue[Open]()
    val closed = mutable.Set[Open]()
    
    exitSet.foreach(exit => {
      for(i <- exit.x - 1 to exit.x + 1){
        for(j <- exit.y - 1 to exit.y + 1){
          if(i >= 0 && i < grid.length){
            if(j >= 0 && j < grid.length){
              grid(i)(j) match {
                case xy : Open if !closed(xy) => 
                xy._distance = 1
                open.enqueue(xy)
                closed += xy
                case _ => 
              }
            }
          }
        }
      }
    })
    
    while(!open.isEmpty){
      val next = open.dequeue
      closed += next
      for(i <- next.x - 1 to next.x + 1){
        for(j <- next.y - 1 to next.y + 1){
          val neighbor = grid(i)(j)
          neighbor match {
            case n : Open => {
              if(!(open contains n) && !(closed contains n)){
                n._distance = next.distance + 1
                open.enqueue(n)
              }
            }
            case _ =>
          }
        }
      }
    }
  }
}