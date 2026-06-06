//app/dtos/mfaotpDto.scala
package dtos

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class MfaDtoRequest(
  otp: String
)

object MfaDtoRequest {
  implicit val reads: Reads[MfaDtoRequest] = Json.reads[MfaDtoRequest]  
}
