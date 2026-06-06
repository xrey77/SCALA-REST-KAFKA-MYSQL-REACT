// src/main/scala/models/Product.scala
package models

import java.time.Instant
import play.api.libs.json.{Json, OFormat, Format, Writes, OWrites} 

case class Product(
  id: Int,
  category_id: Int,
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

object Product {
  implicit val userFormat: Format[Product] = Json.format[Product]
}


case class ProductDetail(id: Int, descriptions: String, qty: Int, unit: String, costprice: BigDecimal, sellprice: BigDecimal)
case class Category(id: Int, name: String)
case class CategoryWithProducts(categoryName: String, products: List[ProductDetail])

object ProductDetail {
  implicit val format: OFormat[ProductDetail] = Json.format[ProductDetail]
}

object CategoryWithProducts {
  implicit val format: OFormat[CategoryWithProducts] = Json.format[CategoryWithProducts]
}

case class PaginatedResult[T](
  data: Seq[T],
  page: Int,
  totalRecords: Int,
  totalPages: Int
)

object PaginatedResult {
  implicit def paginatedResultWrites[T](implicit fmt: Writes[T]): Writes[PaginatedResult[T]] = 
    Json.writes[PaginatedResult[T]]  

  implicit def writes[T](implicit writesT: Writes[T]): OWrites[PaginatedResult[T]] = 
        Json.writes[PaginatedResult[T]]  

}

  