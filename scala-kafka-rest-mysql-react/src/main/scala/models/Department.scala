// src/main/scala/models/Department.scala
package models

import java.util.UUID
import java.time.Instant

case class Department(
  id: UUID,
  dept_name: String,
  createdAt: Instant = Instant.now(),
  updatedAt: Instant = Instant.now()
)
