package com.cory.core.test

import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.ActorMaterializer
import akka.testkit.TestProbe
import com.cory.core.GreetingTopicConsumer
import fabricator.Fabricator
import net.manub.embeddedkafka.{EmbeddedKafka, EmbeddedKafkaConfig}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer

class GreetingTopicConsumerSpec extends BaseSpec[GreetingTopicConsumerSpec] with EmbeddedKafka {
  "A GreetingTopicConsumer" should {
    implicit val materializer = ActorMaterializer()
    implicit val kafkaConfig = EmbeddedKafkaConfig(kafkaPort = 12345)
    val appConfig = new {
      val kafkaBootstrapServer = "the-server"
      val greetingTopic = "the-topic"
    }

    "retrieve latest names from Kafka and aggregate greetings for those names" in {
      withRunningKafka {
        val consumerSettings = ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
          .withBootstrapServers(appConfig.kafkaBootstrapServer)
          .withGroupId("dont-care")
          .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
        val consumer = Consumer.plainSource(consumerSettings, Subscriptions.topics(appConfig.greetingTopic))
        val greetingTranslator = TestProbe()

        val greetingTopicConsumer = system.actorOf(GreetingTopicConsumer.props(consumer, greetingTranslator.testActor))

        val allValues = (1 to 10).map(_ => Fabricator.contact().fullName(true, true))
        allValues.foreach(x => publishStringMessageToKafka(appConfig.greetingTopic, x))
        val expectedValues = allValues.takeRight(4)

      }
    }
  }
}
