
import akka.actor.{Actor, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

import scala.collection.mutable.ArrayBuffer

sealed trait MapReduceMessage
case class WordCount(word: String, count:Int) extends MapReduceMessage
case class MapData(dataList: ArrayBuffer[WordCount]) extends MapReduceMessage
case class ReduceData(reduceDataMap: Map[String, Int]) extends MapReduceMessage
case object Result extends MapReduceMessage


class ReduceActor extends Actor{
  def receive = {
    case message: MapData =>
      print("Received the mapped data, now reducing")
      sender() ! reduce(message.dataList)
      println(sender())
      println("After reducing the data, i got", reduce(message.dataList))
  }

  def reduce(words: IndexedSeq[WordCount]): ReduceData = ReduceData {
    words.foldLeft(Map.empty[String, Int]) { (index, words) =>
      if (index contains words.word)
        index + (words.word -> (index.get(words.word).get + 1))
      else
        index + (words.word -> 1)
    }
  }
}
object Reducer extends App {
  val config = ConfigFactory.load.getConfig("myreduce")

  val system = ActorSystem.create("ReduceSystem", config)

  val reduce = system.actorOf(Props[ReduceActor], "myreduce")
}