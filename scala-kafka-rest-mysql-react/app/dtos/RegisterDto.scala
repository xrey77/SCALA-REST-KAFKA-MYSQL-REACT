package dtos

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class RegisterUserRequest(
  firstname: String,
  lastname: String,
  email: String,   
  mobile: String,
  username: String,
  password: String
)


object RegisterUserRequest {
  implicit val reads: Reads[RegisterUserRequest] = (
    (__ \ "firstname").read[String] and
    (__ \ "lastname").read[String] and
    (__ \ "email").read[String](Reads.email) and // Note: Play JSON has a Reads.email validator
    (__ \ "mobile").read[String] and
    (__ \ "username").read[String](Reads.minLength[String](2)) and
    (__ \ "password").read[String](Reads.minLength[String](8))
  )(RegisterUserRequest.apply _)
}