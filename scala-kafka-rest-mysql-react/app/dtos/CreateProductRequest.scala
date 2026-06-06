// app/dtos/CreateProductRequest.scala
package dtos

import play.api.libs.json._

case class CreateProductRequest(
  descriptions: String,
  qty: Int,
  unit: String,
  costprice: BigDecimal,
  sellprice: BigDecimal,
  saleprice: BigDecimal,
  productpicture: String,  
  alertstocks: Int,
  criticalstocks: Int
)

object CreateProductRequest {
  implicit val format: OFormat[CreateProductRequest] = Json.format[CreateProductRequest]
}
