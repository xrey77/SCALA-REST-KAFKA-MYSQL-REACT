// src/main/scala/models/Product.scala
package models

import java.util.UUID
import java.time.Instant

case class Product(
  id: UUID,
  descriptions: String,
  qty: Int,
  unit: String,
  costprice: BigDecimal = BigDecimal(0.00),
  sellprice: BigDecimal = BigDecimal(0.00),
  saleprice: BigDecimal = BigDecimal(0.00),
  productpicture: String,  
  alertstocks: Int = 0,
  criticalstocks: Int = 0,
  createdAt: Instant = Instant.now(),
  updatedAt: Instant = Instant.now()
)
