// app/models/User.scala
package models

import java.time.Instant

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
