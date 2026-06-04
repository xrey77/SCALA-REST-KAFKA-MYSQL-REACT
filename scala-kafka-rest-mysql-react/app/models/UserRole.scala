// src/main/scala/models/UserRole.scala
package models

import java.util.UUID
import java.time.Instant

case class UserRole(
  userId: UUID,
  roleId: UUID,
  createdAt: Instant = Instant.now()
)
