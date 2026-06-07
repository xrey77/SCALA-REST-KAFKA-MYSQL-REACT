package app

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import services.KafkaProducerService
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat // 1. Added import
import scala.concurrent.ExecutionContextExecutor // 2. Added import
import scala.util.{Failure, Success}

case class MessagePayload(key: String, message: String)

object JsonFormats {
  // 3. Added explicit type: RootJsonFormat[MessagePayload]
  implicit val payloadFormat: RootJsonFormat[MessagePayload] = jsonFormat2(MessagePayload)
}

object RestKafkaApp extends App {
  // 4. Added explicit type: ActorSystem[Nothing] or ActorSystem[Any]
  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "RestKafkaSystem")
  
  // 5. Added explicit type: ExecutionContextExecutor
  implicit val executionContext: ExecutionContextExecutor = system.executionContext

  import JsonFormats._

  val kafkaService = new KafkaProducerService()

  val routes =
    path("api" / "publish") {
      post {
        entity(as[MessagePayload]) { payload =>
          val sendFuture = kafkaService.sendToCentralTopic(payload.key, payload.message)
          // Rest of your route logic goes here...
          complete(StatusCodes.OK)
        }
      }
    }
}