// src/main/scala/models/User.scala
package models

import java.util.UUID
import java.time.Instant

case class User(
  id: UUID,
  departmentId: UUID,
  firstname: String,
  lastname: String,
  email: String,   
  mobile: String,
  username: String,
  password: String,
  userpic: String = "pix.png",  
  isActive: Boolean = true,
  isBlocked: Boolean = false,
  secret: Option[String] = None,     
  qrcodeurl: Option[String] = None,  
  createdAt: Instant = Instant.now(),
  updatedAt: Instant = Instant.now()
)
