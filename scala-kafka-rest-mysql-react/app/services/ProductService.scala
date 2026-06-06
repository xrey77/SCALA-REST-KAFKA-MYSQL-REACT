// app/services/ProductService.scala
package services

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import models.Product
import dtos.UpdateProductRequest
import repositories.ProductRepository
import models.PaginatedResult
import dtos.CreateProductRequest
import models.CategoryWithProducts

trait ProductService {
  def listProducts(page: Int): Future[PaginatedResult[Product]]  
  def searchProducts(page: Int, keyword: String): Future[PaginatedResult[Product]]
  def getProductid(id: Int): Future[Option[Product]]
  def createProduct(request: CreateProductRequest): Future[Product]
  def updateProduct(id: Int, request: CreateProductRequest): scala.concurrent.Future[Option[Product]]
  def getProductDesc(description: String): Future[Option[Product]]
  def productCategory(): Future[List[CategoryWithProducts]]
  def deleteProduct(id: Int): Future[Boolean]
}

@Singleton
class ProductServiceImpl @Inject()(
  productRepository: ProductRepository
)(implicit ec: ExecutionContext) extends ProductService {

  override def listProducts(page: Int): Future[PaginatedResult[Product]] = {
    productRepository.productList(page)
  }

  override def searchProducts(page: Int, keyword: String): Future[PaginatedResult[Product]] = {
    productRepository.productSearch(page, keyword)
  }

  override def getProductid(id: Int): Future[Option[Product]] = {
    productRepository.findByProductId(id)
  }

  def createProduct(request: CreateProductRequest): Future[Product] = {    
     val newProduct = Product(
        id = 0,
        category_id = 1,
        descriptions = request.descriptions,
        qty = request.qty,
        unit = request.unit,
        costprice = request.costprice,
        sellprice = request.sellprice,
        saleprice = request.saleprice,
        productpicture = request.productpicture,
        alertstocks = request.alertstocks,
        criticalstocks = request.criticalstocks
    )
    productRepository.addProduct(newProduct)    
  }  

  override def updateProduct(id: Int, request: CreateProductRequest): Future[Option[Product]] = {
    productRepository.findByProductId(id).flatMap {
      case Some(existing) =>

        val updated = Product(
          id = id,
          category_id = 1,
          descriptions = request.descriptions, 
          qty = request.qty, 
          unit = request.unit,
          costprice = request.costprice,
          sellprice = request.sellprice,
          saleprice = request.saleprice,
          productpicture = request.productpicture,
          alertstocks = request.alertstocks,
          criticalstocks = request.criticalstocks
        )

        productRepository.updateProduct(id, updated).map(Some(_))  

      case None => Future.successful(None)
    }
  }

  override def getProductDesc(description: String): Future[Option[Product]] = {
      productRepository.findDescription(description) 
  }

  override def productCategory(): Future[List[CategoryWithProducts]] = {  
     productRepository.fetchMasterDetails()
  }

  override def deleteProduct(id: Int): Future[Boolean] = {
    productRepository.deleteProduct(id)
  }

}