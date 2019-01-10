package wordcount

import akka.actor.{Actor, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

import scala.collection.mutable.{ArrayBuffer, HashMap}

sealed trait MapReduceMessage
case class WordCount(word: String, count:Int) extends MapReduceMessage
case class MapData(dataList: ArrayBuffer[WordCount]) extends MapReduceMessage
case class ReduceData(reduceDataMap: Map[String, Int]) extends MapReduceMessage
case object Result extends MapReduceMessage

class AggregateActor extends Actor {
  var finalReducedMap = new HashMap[String, Int]
  def receive: Receive = {
    case msg: ReduceData => aggregateInMemoryReduce(msg.reduceDataMap)
      println("Received the reduced data! not aggregating")
    case Result => sender() ! finalReducedMap.toString()
      println(finalReducedMap.toString())
  }
  def aggregateInMemoryReduce(reducedList: Map[String, Int]): Unit = {
    for ((key,value) <- reducedList) {
      if (finalReducedMap contains key)
        finalReducedMap(key) = (value + finalReducedMap.get(key).get)
      else {
        finalReducedMap += (key -> value)
      }
    }
  }
}
object aggregate extends App{
  val config = ConfigFactory.load().getConfig("myagg")

  val system = ActorSystem.create("AggregateSystem", config)

  val aggActor = system.actorOf(Props[AggregateActor], "myagg")
}