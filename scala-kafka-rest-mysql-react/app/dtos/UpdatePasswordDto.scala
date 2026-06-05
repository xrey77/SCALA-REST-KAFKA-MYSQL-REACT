//app/dtos/UpdatePasswordDto.scala
package dtos

import play.api.libs.json._
import play.api.libs.functional.syntax._


case class UpdatePasswordRequest(
  password: String
)

object UpdatePasswordRequest {
  implicit val reads: Reads[UpdatePasswordRequest] = Json.reads[UpdatePasswordRequest]  
}
