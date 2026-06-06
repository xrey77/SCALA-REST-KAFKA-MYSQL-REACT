// app/services/SaleService.scala
package services

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import models.Sale
import repositories.SaleRepository

trait SaleService {
    def listSales(): Future[Seq[Sale]]
}

@Singleton
class SaleServiceImpl @Inject()(
  saleRepository: SaleRepository
)(implicit ec: ExecutionContext) extends SaleService {

  override def listSales(): Future[Seq[Sale]] = {
    saleRepository.salesList()
  }

}