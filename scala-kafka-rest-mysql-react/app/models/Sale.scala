// app/models/sale.scala
package models

import java.time.Instant
import play.api.libs.json._

case class Sale(
  id: Int,
  salesamount: BigDecimal = BigDecimal(0.00),
  salesdate: Instant = Instant.now(),
)

object Sale {
  implicit val userFormat: Format[Sale] = Json.format[Sale]
}


case class Sales(
  salesamount: BigDecimal = BigDecimal(0.00),
  salesdate: Instant = Instant.now(),
)


object Sales {
  implicit val userFormat: Format[Sale] = Json.format[Sale]
}
