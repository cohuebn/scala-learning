import akka.actor.ActorSystem
import akka.testkit.TestKit
import com.typesafe.config.ConfigFactory

class BaseSpec(name: String)
  extends TestKit(ActorSystem(name, ConfigFactory.parseString("""akka.loggers = ["akka.testkit.TestEventListener"]"""))) {
}