import akka.actor.{Actor, ActorSystem, Props}
import java.util.Scanner
import akka.actor._
import com.typesafe.config.ConfigFactory
import scala.io.StdIn


object RemoteChatApp extends App {
  var Sender: ActorRef = _
  val config = ConfigFactory.load().getConfig("RemoteChatConfig")
  val system = ActorSystem("RemoteChatSystem", config)
  val remote_chat_actor = system.actorOf(Props[RemoteChatActor], "Remote_Chat_Actor")
  val scanner = new Scanner(System.in)
  println("Waiting for message from Local Machine")

  while(true) {
    val input = scanner.nextLine()
    Sender ! Get(input)
  }
}
class RemoteChatActor extends Actor {
  def receive = {
    case msg: Send =>
      println("Message Received from Local Machine: " + msg.Message)
      RemoteChatApp.Sender = sender
  }
}