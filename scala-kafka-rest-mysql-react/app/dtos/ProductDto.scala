// app/dtos/ProductDto.scala
package dtos

import play.api.libs.json._
import play.api.libs.functional.syntax._

// case class UpdateProductRequest(
//   descriptions: String,
//   qty: Int, 
//   unit: String, 
//   costprice: BigDecimal,
//   sellprice: BigDecimal,
//   saleprice: BigDecimal,
//   productpicture: String,
//   alertstocks: Int,
//   criticalstocks: Int
// ) 
// {
//   // Method to turn this DTO into your actual Database Model
//   def toModel(productId: Int): models.Product = models.Product(
//     id = productId,
//     descriptions = this.descriptions,
//     qty = this.qty,
//     unit = this.unit,
//     costprice = this.costprice,
//     sellprice = this.sellprice,
//     saleprice = this.saleprice,
//     productpicture = this.productpicture,
//     alertstocks = this.alertstocks,
//     criticalstocks = this.criticalstocks
//   )
// }

case class UpdateProductRequest(
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

object UpdateProductRequest {
  implicit val reads: Reads[UpdateProductRequest] = (
    (__ \ "descriptions").read[String] and
    (__ \ "qty").read[Int] and
    (__ \ "unit").read[String] and
    (__ \ "costprice").read[BigDecimal] and
    (__ \ "sellprice").read[BigDecimal] and
    (__ \ "saleprice").read[BigDecimal] and
    (__ \ "productpicture").read[String] and
    (__ \ "alertstocks").read[Int] and
    (__ \ "criticalstocks").read[Int] 
  )(UpdateProductRequest.apply _)
}
