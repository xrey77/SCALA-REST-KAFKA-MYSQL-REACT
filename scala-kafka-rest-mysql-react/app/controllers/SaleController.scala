// app/controllers/SaleController.scala
package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import scala.concurrent.{ExecutionContext, Future}
import services.SaleService

@Singleton
class SaleController @Inject()(
  val controllerComponents: ControllerComponents,
  saleService: SaleService
)(implicit ec: ExecutionContext) extends BaseController {

  // GET /api/v1/saleslist
  def listsalesdata(): Action[AnyContent] = Action.async {
     saleService.listSales().map { sales =>
        Ok(Json.toJson(sales))
    }
  }
}