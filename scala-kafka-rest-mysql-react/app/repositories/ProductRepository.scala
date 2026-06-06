// app/repositories/ProductRepository.scala
package repositories

import javax.inject.{Inject, Singleton}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import scala.concurrent.{ExecutionContext, Future}
import scala.math.BigDecimal.RoundingMode
import slick.jdbc.JdbcProfile
import models.ProductDetail
import models.CategoryWithProducts
import java.time.Instant
import models.{Category, Product}
import models.PaginatedResult

trait ProductRepository {
  def productList(offset: Int)(implicit ec: ExecutionContext): Future[PaginatedResult[Product]]
  def findByProductId(id: Int): Future[Option[Product]]
  def findDescription(description: String): Future[Option[Product]]
  def addProduct(prod: Product): Future[Product]
  def updateProduct(id: Int, product: Product): Future[Product]
  def deleteProduct(id: Int): Future[Boolean]
  def productSearch(page: Int, keyword: String)(implicit ec: ExecutionContext): Future[PaginatedResult[Product]]
  def fetchMasterDetails(): Future[List[CategoryWithProducts]]
}

@Singleton
class ProductRepositoryImpl @Inject()(
  protected val dbConfigProvider: DatabaseConfigProvider   
)(implicit ec: ExecutionContext) 
   extends ProductRepository
    with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._
  private val products = TableQuery[ProductsTable]
  private val categories = TableQuery[CategoriesTable]

  class CategoriesTable(tag: Tag) extends Table[Category](tag, "categories") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def * = (id, name) <> (Category.tupled, Category.unapply)
  }

  class ProductsTable(tag: Tag) extends Table[Product](tag, "products") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def categoryId = column[Int]("category_id")     
    def descriptions = column[String]("descriptions")    
    def qty = column[Int]("qty")
    def unit = column[String]("unit")
    def costprice = column[BigDecimal]("costprice")
    def sellprice = column[BigDecimal]("sellprice")
    def saleprice = column[BigDecimal]("saleprice")
    def productpicture = column[String]("productpicture")
    def alertstocks = column[Int]("alertstocks")    
    def criticalstocks = column[Int]("criticalstocks")

    def category = foreignKey("fk_product_category", categoryId, categories)(_.id, onDelete=ForeignKeyAction.Cascade)
    def * = (id, categoryId, descriptions, qty, unit, costprice, sellprice, saleprice, productpicture, alertstocks, criticalstocks) <> ((Product.apply _).tupled, Product.unapply)
  }

  private def calculateTotalPages(totalRecords: Int, limit: Int): Int = {
    if (totalRecords == 0) 1 
    else java.lang.Math.ceil(totalRecords.toDouble / limit).toInt
  }

  override def productList(page: Int)(implicit ec: ExecutionContext): Future[PaginatedResult[Product]] = {    
    val limit = 5
    val offset = (page - 1) * limit

    val dataQuery = products.sortBy(_.id).drop(offset).take(limit).result
  
    val countQuery = products.length.result

    db.run(dataQuery).zip(db.run(countQuery)).map { case (data, total) =>
     PaginatedResult(
       data = data,
       page = page,
       totalRecords = total,
       totalPages = calculateTotalPages(total, limit)
     )
    }    
  }

  override def productSearch(page: Int, keyword: String)(implicit ec: ExecutionContext): Future[PaginatedResult[Product]] = {    
    val limit = 5
    val offset = (page - 1) * limit

    val wildcardKeyword = s"%$keyword%"
    
    val filteredProducts = products.filter(_.descriptions like wildcardKeyword)

    val dataQuery = filteredProducts.sortBy(_.id).drop(offset).take(limit).result
    val countQuery = filteredProducts.length.result

    db.run(dataQuery).zip(db.run(countQuery)).map { case (data, total) =>
        PaginatedResult(
        data = data,
        page = offset,
        totalRecords = total,
        totalPages = calculateTotalPages(total, limit)
        )
    }    
  }

  override def findByProductId(id: Int): Future[Option[Product]] = {
    db.run(products.filter(_.id === id).result.headOption)    
  }

  override def findDescription(description: String): Future[Option[Product]] = {
    db.run(products.filter(_.descriptions === description).result.headOption)    
  }

  override def addProduct(prod: Product): Future[Product] = {
    val insertAction = (products returning products.map(_.id)
      into ((insertedProduct, originalId) => insertedProduct.copy(id = originalId))
    ) += prod

    db.run(insertAction)
  }
  
  override def updateProduct(id: Int, product: Product): Future[Product] = {
    val query = products
      .filter(_.id === id)
      .map(u => (u.descriptions, u.qty, u.unit, u.costprice, u.sellprice, u.saleprice, u.productpicture, u.alertstocks, u.criticalstocks))
      .update((product.descriptions, product.qty, product.unit, product.costprice, product.sellprice, product.saleprice, product.productpicture, product.alertstocks, product.criticalstocks))

    db.run(query).map { rowsAffected =>
      if (rowsAffected > 0) product.copy(id = id)
      else throw new Exception(s"Product with id $id not found")
    }
  }

  override def deleteProduct(id: Int): Future[Boolean] = {
    db.run(products.filter(_.id === id).delete).map(_ > 0)
  }


// def fetchMasterDetails(): Future[List[CategoryWithProducts]] = {
//   val query = categories
//     .join(products)
//     .on(_.id === _.categoryId)
//     .map { case (cat, prod) => 
//       (cat.name, prod.id, prod.descriptions, prod.qty, prod.unit, prod.costprice, prod.sellprice) 
//     }

//   db.run(query.result).map { rawRows =>
//     // 1. Group the flat rows by the category name
//     rawRows.groupBy(_._1).map { case (categoryName, rowsForCategory) =>
      
//       // 2. Map each row in the group to a ProductDetail object
//       val productDetails = rowsForCategory.map { case (_, id, desc, qty, unit, cost, sell) =>
//         ProductDetail(id, desc, qty, unit, cost, sell)
//       }.toList

//       // 3. Create the CategoryWithProducts using the 2 expected arguments
//       CategoryWithProducts(categoryName, productDetails)
//     }.toList
//   }
// }


  def fetchMasterDetails(): Future[List[CategoryWithProducts]] = {
    val query = categories
      .join(products)
      .on(_.id === _.categoryId)
      .map { case (cat, prod) => 
        (cat.name, prod.id, prod.descriptions, prod.qty, prod.unit, prod.costprice, prod.sellprice) 
      }

    db.run(query.result).map { rawRows =>
      rawRows.groupBy(_._1).map { case (categoryName, rows) =>
        val details = rows.map { case (_, prodId, desc, qty, unit, cost, sell) =>
          ProductDetail(prodId, desc, qty, unit, cost, sell)
        }.toList
        CategoryWithProducts(categoryName, details)
      }.toList
    }
  }

}