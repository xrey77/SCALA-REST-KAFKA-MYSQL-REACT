package dtos

import play.api.libs.json._
import play.api.libs.functional.syntax._


case class LoginUserRequest(
  username: String,
  password: String, 
)

object LoginUserRequest {
  implicit val reads: Reads[LoginUserRequest] = (
    (__ \ "username").read[String](Reads.minLength[String](2)) and
    (__ \ "password").read[String](Reads.minLength[String](8))
  )(LoginUserRequest.apply _)
}
