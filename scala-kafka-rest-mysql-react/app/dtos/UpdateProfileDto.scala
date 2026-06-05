package dtos

import play.api.libs.json._
import play.api.libs.functional.syntax._


case class UpdateProfileRequest(
  firstname: String,
  lastname: String, 
  mobile: String
)

object UpdateProfileRequest {
  implicit val reads: Reads[UpdateProfileRequest] = (
    (__ \ "firstname").read[String] and
    (__ \ "lastname").read[String] and
    (__ \ "mobile").read[String]
  )(UpdateProfileRequest.apply _)
}
