package com.cory.core.test

import akka.kafka.Subscriptions
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.kafka.testkit.scaladsl.{EmbeddedKafkaLike, ScalatestKafkaSpec}
import akka.stream.scaladsl.{Keep, Sink, Source}
import akka.testkit.TestProbe
import com.cory.core.GreetingTopicConsumer.LatestGreetingsRequest
import com.cory.core.{Greeting, GreetingTopicConsumer}
import fabricator.Fabricator
import net.manub.embeddedkafka.{EmbeddedKafka, EmbeddedKafkaConfig}
import org.apache.kafka.clients.producer.ProducerRecord
import org.scalatest.concurrent.{Eventually, ScalaFutures}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration._
import scala.language.postfixOps

class GreetingTopicConsumerSpec extends ScalatestKafkaSpec(kafkaPort = 12345)
  with EmbeddedKafkaLike
  with Matchers
  with WordSpecLike
  with ScalaFutures
  with Eventually
  with BeforeAndAfterAll {

  implicit val patience = PatienceConfig(25 seconds, 1 second)

  override def createKafkaConfig: EmbeddedKafkaConfig = EmbeddedKafkaConfig(kafkaPort, 12346)

  override def beforeAll = {
    super.beforeAll()
    cleanUp()
  }

  "A GreetingTopicConsumer" should {
    "retrieve latest names from Kafka and aggregate greetings for those names" in {
      val greetingTopic = createTopic()
      val consumerSettings = consumerDefaults.withGroupId(createGroupId())
      val consumer = Consumer.plainSource(consumerSettings, Subscriptions.topics(greetingTopic))
      val (control, _) = consumer.preMaterialize()
      val greetingTranslator = TestProbe()
      val greetingTopicConsumer = system.actorOf(GreetingTopicConsumer.props(consumer, greetingTranslator.testActor))

      val request = LatestGreetingsRequest(2)
      greetingTopicConsumer ! request

      Source(1 to 10)
        .map { _ =>
          new ProducerRecord(greetingTopic, 0, DefaultKey, Fabricator.contact().fullName(true, true))
        }
        .runWith(Producer.plainSink(producerDefaults))
        .futureValue

      val done = consumer.take(5).runWith(Sink.seq)
      done.onComplete {
        x =>
          log.info(s"Look at $x")
          control.shutdown().futureValue
      }
//      val firstTwo = greetingTranslator.receiveN(2)
//      log.info(s"$firstTwo")
      //      val result = consumer.take(10).runWith(Sink.seq)
      //      log.info(s"The result: ${result.futureValue}")
    }
  }
}
