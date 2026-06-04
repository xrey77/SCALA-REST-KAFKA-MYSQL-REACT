package models

import java.util.UUID
import java.time.Instant

case class Sale(
  id: UUID,
  salesamount: BigDecimal = BigDecimal(0.00),
  salesdate: Instant = Instant.now(),
)
