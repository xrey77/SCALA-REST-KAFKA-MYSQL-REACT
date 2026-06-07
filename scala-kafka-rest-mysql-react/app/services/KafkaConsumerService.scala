// app/services/KafkaConsumerService.scala
package services

import cats.effect.IO
import fs2.kafka._
import scala.concurrent.duration._

object KafkaConsumerService {

  // Expose the consumer execution logic as a standard IO block
  def start: IO[Unit] = {
    val consumerSettings = ConsumerSettings[IO, String, String]
      .withBootstrapServers("localhost:9092")
      .withGroupId("scala-consumer-group-v1")
      .withAutoOffsetReset(AutoOffsetReset.Earliest)

    KafkaConsumer.stream(consumerSettings)
      .subscribeTo("central-topic")
      .records
      .mapAsync(16) { committable =>
        IO(println(s"Processing Record: Key=${committable.record.key}, Value=${committable.record.value}"))
          .as(committable.offset)
      }
      .through(commitBatchWithin(500, 15.seconds))
      .compile
      .drain
  }
}








// package services

// import cats.effect.{IO, IOApp, ExitCode}
// import fs2.kafka._
// import scala.concurrent.duration._

// object KafkaConsumerService extends IOApp {

//   override def run(args: List[String]): IO[ExitCode] = {
    
//     // 1. Define Confluent Connection Settings
//     val consumerSettings = ConsumerSettings[IO, String, String]
//       .withBootstrapServers("localhost:9092")
//       .withGroupId("scala-consumer-group-v1")
//       .withAutoOffsetReset(AutoOffsetReset.Earliest)
//     //   .withProperty("security.protocol", "SASL_SSL") // Needed for Confluent Cloud
//     //   .withProperty("sasl.mechanism", "PLAIN")
//     //   .withProperty("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required username='<API_KEY>' password='<API_SECRET>';")

//     // 2. Build and Compile the Stream
//     val stream = KafkaConsumer.stream(consumerSettings)
//       .subscribeTo("central-topic")
//       .records
//       .mapAsync(16) { committable =>
//         IO(println(s"Processing Record: Key=${committable.record.key}, Value=${committable.record.value}"))
//           .as(committable.offset) // Return offset token for committing
//       }
//       .through(commitBatchWithin(500, 15.seconds)) // Batch commits for performance

//     // 3. Execute continuously
//     stream.compile.drain.as(ExitCode.Success)
//   }
// }
