import akka.actor.{Actor, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

class Fibo_Local extends Actor {
  val num = 950000
  val fibRemote = context.actorSelection("akka.tcp://FiboSystem@10.124.15.58:2300/user/myfibo")
  def receive = {

    case "Start" => println("Waiting for result from remote node")
      fibRemote ! num
    case msg: BigInt =>
      println(s"Result =  $msg")
    case msg: Double =>
      println(s"Computation duration = $msg")

    case default =>
      println("Blunder!!!")
  }

}
object FibonacciTailRecursive extends App {

  val config = ConfigFactory.load()

  val system = ActorSystem.create("Fibo_Remote_System", config)

  val fiboLocal = system.actorOf(Props[Fibo_Local], "myremotefibo")

  fiboLocal ! "Start"


}
