import akka.testkit.EventFilter
import com.cory.core.Printer
import com.cory.core.Printer.Greeting
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.language.postfixOps

class PrinterSpec(name: String) extends BaseSpec(name)
  with Matchers
  with WordSpecLike
  with BeforeAndAfterAll {

  override def afterAll: Unit = {
    shutdown(system)
  }

  def this() = this("PrinterSpec")

  "A Printer Actor" should {
    "print greeting messages" in {
      val printer = system.actorOf(Printer.props)

      val message = "hello, steve mcqueen"
      EventFilter.info(pattern = "hello, steve mcqueen", occurrences = 1) intercept {
        printer ! Greeting(message)
      }
    }
  }
}
