// app/models/User.scala
package models

import java.time.Instant
import play.api.libs.json._

case class User(
  id: Int,
  roleId: Int,
  departmentId: Int,
  firstname: String,
  lastname: String,
  email: String,   
  mobile: String,
  username: String,
  password: String,
  userpic: String = "pix.png",  
  isActive: Boolean = true,
  isBlocked: Boolean = false,
  mailtoken: Int,
  secret: Option[String] = None,     
  qrcodeurl: Option[String] = None
  // createdAt: Instant,
  // updatedAt: Instant   
)

object User {
  implicit val userFormat: Format[User] = Json.format[User]
}

case class UserProfile(
  id: Int,
  firstname: String,
  lastname: String,
  mobile: String,
)

object UserProfile {
  implicit val userProfileFormat: OFormat[UserProfile] = Json.format[UserProfile]
}


case class MfaActivationResult(
  user: User, 
  qrCodeUrl: Option[String]
)


// case class UpdatePassword(
//   id: Int,
//   password: String
// )

// object UpdatePassword {
//   implicit val format: OFormat[UpdatePasswordRequest] = Json.format[UpdatePasswordRequest]
// }