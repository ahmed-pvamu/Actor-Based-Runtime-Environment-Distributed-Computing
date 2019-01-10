import akka.actor.{Actor, ActorSystem, Props}
import java.util.Scanner
import akka.actor._
import com.typesafe.config.ConfigFactory
import scala.io.StdIn

case class Send(Message: String)
case class Get(Message: String)

object LocalChatApp extends App{

  val config = ConfigFactory.load().getConfig("LocalChatConfig")
  val system = ActorSystem("LocalChatSystem", config)
  val local_chat_actor = system.actorOf(Props[LocalChatActor], "Local_Chat_Actor")
  val scanner = new Scanner(System.in)

 println("Send message to remote")

  while (true) {
    val input = scanner.nextLine()
    local_chat_actor ! Send(input)
  }
}
class LocalChatActor extends Actor {
  val remote = context.actorSelection("akka.tcp://RemoteChatSystem@10.124.12.139:2555/user/Remote_Chat_Actor")
  def receive = {
    case msg: Send =>
      println("Sending message to Remote Machine")
      remote ! msg
    case msg: Get =>
      println("Message Received from Remote Machine: " + msg.Message)
  }
}
