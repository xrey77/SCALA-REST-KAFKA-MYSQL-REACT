// app/controllers/ProductController.scala
package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import scala.concurrent.{ExecutionContext, Future}
import services.ProductService
import models.Product
import dtos.UpdateProductRequest
import dtos.CreateProductRequest
import models.CategoryWithProducts

@Singleton
class ProductController @Inject()(
  val controllerComponents: ControllerComponents,
  productService: ProductService
)(implicit ec: ExecutionContext) extends BaseController {

  // GET /api/v1/productlist/1
  def listproducts(page: Int): Action[AnyContent] = Action.async {
    if (page < 1) {
      Future.successful(BadRequest(Json.obj("message" -> "Invalid pagination parameters")))
    } else {
      productService.listProducts(page).map { products =>
        Ok(Json.toJson(products))
      }
    }
  }


  // GET /api/v1/productsearch/1/keyword
  def searchproducts(page: Int, keyword: String): Action[AnyContent] = Action.async {
    if (page < 1) {
      Future.successful(BadRequest(Json.obj("message" -> "Invalid pagination parameters")))
    } else {
      productService.searchProducts(page, keyword).map { products =>
        Ok(Json.toJson(products))
      }
    }
  }


  // GET /api/v1/productbycategory
  def getProductCategory(): Action[AnyContent] = Action.async {
    productService.productCategory().map { categories =>
      Ok(Json.toJson(categories))
    }
  }

  // GET /api/v1/getproductbyid/:id
  def getProductId(id: Int): Action[AnyContent] = Action.async {
    productService.getProductid(id).map {
      case Some(product) => Ok(Json.toJson(product))
      case None => NotFound(Json.obj("message" -> "Product not found", "id" -> id))
    }
  }


  // PATCH /api/v1/updateuser/:id
  def updateProduct(id: Int): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[CreateProductRequest] match {
      case JsSuccess(createRequest, _) =>
        productService.updateProduct(id, createRequest).map {
          case Some(product) => 
            Ok(Json.obj("message" -> "Product has been updated successfully."))
          case None => 
            NotFound(Json.obj("message" -> "Product not found"))
        }
      case JsError(errors) =>
        Future.successful(BadRequest(Json.obj("message" -> "Invalid request")))
    }
  }


  // POST /api/v1/addproduct
  def addProduct: Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[CreateProductRequest].fold(
      errors => {
        Future.successful(BadRequest(Json.obj("status" -> "Error", "message" -> JsError.toJson(errors))))
      },
      createRequest => {
        productService.createProduct(createRequest).map { savedProduct =>
          val responseJson = Json.obj(
            "message" -> "New Product has been added successfully."
          )                        

          Created(responseJson)
        }.recover {
          case ex: Exception => InternalServerError(Json.obj("status" -> "Error", "message" -> ex.getMessage))
        }
      }
    )  
  }

  // DELETE /api/v1/deleteproduct/:id
  def deleteproduct(id: Int): Action[AnyContent] = Action.async {
    productService.deleteProduct(id).map {
      case true => Ok(Json.obj("message" -> s"Product ID: ${id} has been deleted successfully."))
      case false => NotFound(Json.obj("message" -> "User not found"))
    }
  }
}