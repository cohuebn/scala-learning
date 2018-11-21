import akka.testkit.TestProbe
import com.cory.core.Greeter
import com.cory.core.Greeter._
import com.cory.core.Printer.Greeting
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration._
import scala.language.postfixOps

class GreeterSpec(name: String) extends BaseSpec(name)
  with Matchers
  with WordSpecLike
  with BeforeAndAfterAll {

  def this() = this("GreeterSpec")

  override def afterAll: Unit = {
    shutdown(system)
  }

  "A Greeter Actor" should {
    "send a greeting message to the printer" in {
      val message = "hello"
      val printer = TestProbe()
      val helloGreeter = system.actorOf(Greeter.props(message, printer.ref))

      val person = "Akka"
      helloGreeter ! Greet(person)

      printer.expectMsg(500 millis, Greeting(message + ", " + person))
    }
  }
}
