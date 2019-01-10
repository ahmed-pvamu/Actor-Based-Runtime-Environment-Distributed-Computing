import akka.actor.{Actor, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

class fibonacci extends Actor{

  def receive: Receive = {
    case msg: Int  =>
      val startTime: Double = System.currentTimeMillis()
      println(startTime)
      println(s"Welcome to fibonacci calculator $msg")
      sender() ! fib(msg)
      println(System.currentTimeMillis())
      sender ! (System.currentTimeMillis - startTime)
    case default =>
      println("Kindly send me an Integer or a BigInt")
  }

  def fib(x: Int): BigInt = {
    def fiboCalc(x: Int, prev: BigInt = 0, next: BigInt = 1): BigInt = x match {
      case 0 => prev
      case 1 => next
      case _ => fiboCalc(x - 1, next, (next + prev))
    }

    fiboCalc(x)
  }
}


object FibonacciTailRecursive extends App {

 val config = ConfigFactory.load()

  val startTime = System.currentTimeMillis()
  val system = ActorSystem.create("FiboSystem", config)

  val fiboActor = system.actorOf(Props[fibonacci], "myfibo")




}