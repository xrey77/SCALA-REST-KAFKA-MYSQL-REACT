// app/MainApp.scala
package app

import cats.effect.{IO, IOApp, ExitCode}
import cats.implicits._ // Added to enable .parTupled
import services.KafkaConsumerService

object MainApp extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    val dummyWebServer = IO(println("Starting HTTP Server...")) *> IO.never

    // Both effects must be IO types for parTupled to work
    (KafkaConsumerService.start, dummyWebServer).parTupled.as(ExitCode.Success)
  }
}
