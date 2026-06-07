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


case class UserProfilepic(
  id: Int,
  userpic: String,
)

object UserProfilepic {
  implicit val userProfilepicFormat: OFormat[UserProfilepic] = Json.format[UserProfilepic]
}


case class MfaActivationResult(
  user: User, 
  qrCodeUrl: Option[String]
)