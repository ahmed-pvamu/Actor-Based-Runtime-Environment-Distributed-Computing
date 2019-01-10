import com.typesafe.config.ConfigFactory
import akka.actor.{Actor, ActorSystem, Props}
import scala.collection.mutable.ArrayBuffer

sealed trait MapReduceMessage
case class WordCount(word: String, count:Int) extends MapReduceMessage
case class MapData(dataList: ArrayBuffer[WordCount]) extends MapReduceMessage
case class ReduceData(reduceDataMap: Map[String, Int]) extends MapReduceMessage
case object Result extends MapReduceMessage

class MapActor extends Actor{
  val stop_word = List("a", "am", "an", "and", "are", "as",
    "at", "be", "do", "go", "if", "in", "is", "it", "of", "on", "the",
    "to")
  val defaultCount: Int = 1
  def receive = {
    case message: String =>
      println("Received Data, now counting and indexing words")
      sender() ! evaluateExpression(message)
      println(sender())
      println("After counting and grouping, we have = ", evaluateExpression(message))
  }
  def evaluateExpression(line: String): MapData = MapData {
    line.split("""\s+""").foldLeft(ArrayBuffer.empty[WordCount]) {
      (index, word) =>
        if(!stop_word.contains(word.toLowerCase))
          index += WordCount(word.toLowerCase, 1)
        else
          index
    }
  }
}
object Mapper extends App{
  val config = ConfigFactory.load()

  val system = ActorSystem.create("MapperSystem", config)

  val map = system.actorOf(Props[MapActor], "mymap")
}

