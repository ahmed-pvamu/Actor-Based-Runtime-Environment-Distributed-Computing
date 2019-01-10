import akka.actor.{Actor, ActorSystem, Props}
import akka.routing.RoundRobinRoutingLogic
import akka.routing.RoundRobinPool
import java.util.concurrent._
import java.util.concurrent.TimeUnit
import java.io._


sealed trait myMsg
case object Calculate extends myMsg
case class Work(iterations: Int) extends myMsg
case class Result(value: Int) extends myMsg
case class PiEstimate(pi: Double, duration: Double)


//Main method
object PiEstimate extends App {
  //Declare initial values
  val nWorkers = 1
  val nIterations = 10000000
  val system = ActorSystem("PiEstimatorSystem") //Create Akka system
  val master = system.actorOf(Props(new Master(nWorkers, nIterations)))
  println("Starting Calculations!")


//  println(master)
    master ! Calculate


}
//Master Class
class Master(nWorkers: Int, nIterations: Int) extends Actor{

  var pi: Double = _ // Store pi
  var quadSum: Int = _ //the total number of points inside the quadrant
  var numResults: Int = _ //number of results returned
  val startTime: Double = System.currentTimeMillis() //calculation start time

  //Create a group of worker actors
  val workerRouter = context.actorOf(Props[Worker].withRouter(RoundRobinPool(nWorkers)), name = "workerRouter")
  val listener = context.actorOf(Props[Listener], name = "listener")

  def receive = {
    //Tell workers to start the calculation
    //For each worker a message is sent with the number of iterations it is to perform,
    //iterations are split up between the number of workers.

    case Calculate => for(i <- 0 until nWorkers) workerRouter ! Work(nIterations/nWorkers)


    //Receive the results from the workers
    case Result(value) =>
      //Add up the total number of points in the circle from each worker
      quadSum += value
      //Total up the number of results which have been received, this should be 1 for each worker
      numResults += 1

      if(numResults == nWorkers) { //Once all results have been collected
        //Calculate pi
        pi = (4.0 * quadSum) / nIterations
        //Send the results to the listener to output
        listener ! PiEstimate(pi, duration = System.currentTimeMillis - startTime)
        context.stop(self)
      }
  }
}
//Worker
class Worker extends Actor {
  //Performs the calculation
  def calculatePi(iterations: Int): Int = {

    val r = new scala.util.Random // Create random number generator
    var pointWithin: Int = 0 //Store number of points within circle

    for(i <- 0 to iterations){
      //Generate random point
      val X = r.nextFloat()
      val Y = r.nextFloat()

      //Determine whether or not the point is within the circle
      if(((X * X) + (Y * Y)) < 1.0)
        pointWithin += 1
    }
    pointWithin //return the number of points within the circle
  }
  def receive = {
    //Starts the calculation then returns the result
    case Work(iterations) => sender ! Result(calculatePi(iterations))

  }
}

//Listener
class Listener extends Actor{ //Recieves and prints the final result
  def receive = {
    case PiEstimate(pi, duration) =>
      //Print the results
      println("\n\tPi approximation: \t\t%s\n\tCalculation time: \t%s".format(pi, duration))
      //Print to a CSV file
       val pw: FileWriter = new FileWriter("../../../..//Results/Scala_Results.csv", true)
       pw.append(duration.toString())
       pw.append("\n")
       pw.close()
       context.system.terminate()

  }
}