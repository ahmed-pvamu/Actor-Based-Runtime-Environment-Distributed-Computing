import akka.actor.{Actor, ActorSystem, Props}

import scala.concurrent.duration._
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Await
import akka.pattern.ask
import akka.routing.FromConfig
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import scala.io.Source

sealed trait MapReduceMessage

case class WordCount(word: String, count:Int) extends MapReduceMessage
case class MapData(dataList: ArrayBuffer[WordCount]) extends MapReduceMessage
case class ReduceData(reduceDataMap: Map[String, Int]) extends MapReduceMessage
case class Result()

import akka.actor.{Actor, Props}

class MasterActor extends Actor {
    val mapActor = context.actorOf(FromConfig.props(Props[MasterActor]), name = "map")
   val reduceActor = context.actorOf(FromConfig.props(Props[MasterActor]), name = "reduce")
  val aggregateActor = context.actorOf(FromConfig.props(Props[MasterActor]), name = "aggregate")

  def receive: Receive = {
    case msg: String => println(s"I got this one $msg")
      mapActor ! msg
    case mapData: MapData =>
      println(reduceActor)
     reduceActor ! mapData
    case reduceData: ReduceData =>
      aggregateActor ! reduceData
      println(aggregateActor)
    case Result => aggregateActor forward Result
  }
}

object Main_Counter extends App {
  val config = ConfigFactory.load()
  val system = ActorSystem("MapReduceApp", config)
  val master = system.actorOf(Props[MasterActor], name = "master")
  implicit val timeout = Timeout(50.seconds)
  var totalline = 0
  var filename = "F:\\WordCount\\fileopen.txt"

  println("Starting to read and process lines")
 Thread.sleep(2000)
    for (msg <- Source.fromFile(filename).getLines){
      totalline += 1
      master ! msg
    }
  println(s"Total number of lines processes = : $totalline")
  Thread.sleep(5000)

  master ! new Result

//  val future = (master ? Result).mapTo[String]
//  val result = Await.result(future, timeout.duration)
//  println(result)
//  system.terminate()
  println("Done")

}