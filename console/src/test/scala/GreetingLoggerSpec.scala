import akka.testkit.{EventFilter, TestProbe}
import com.cory.console.GreetingLogger
import com.cory.core.Greeting
import com.cory.core.GreetingTranslator.GreetingRequest
import com.cory.core.test.BaseSpec
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class GreetingLoggerSpec extends BaseSpec("GreetingLoggerSpec")
  with Matchers
  with WordSpecLike
  with BeforeAndAfterAll {

  override def afterAll() {
    shutdown(system)
  }

  "A Greeting Logger" should {
    "use the logger to print greetings" in {
      val greetingLogger = system.actorOf(GreetingLogger.props(testActor))

      val message = "yo dawg"
      EventFilter.info(pattern = message, occurrences = 1) intercept {
        greetingLogger ! Greeting(message)
      }
    }

    "request greetings from the translator" in {
      val translator = TestProbe()
      val greetingLogger = system.actorOf(GreetingLogger.props(translator.testActor))

      val request = GreetingRequest("whateva", "thename")
      greetingLogger ! request

      translator.expectMsg(request)
    }
  }
}
