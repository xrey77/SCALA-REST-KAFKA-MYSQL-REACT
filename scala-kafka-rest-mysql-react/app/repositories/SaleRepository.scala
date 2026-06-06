// app/repositories/SaleRepository.scala
package repositories

import javax.inject.{Inject, Singleton}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.JdbcProfile
import models.Sale
import java.time.Instant
import java.sql.Timestamp

trait SaleRepository {
    def salesList(): Future[Seq[Sale]]
}

@Singleton
class SaleRepositoryImpl @Inject()(
  protected val dbConfigProvider: DatabaseConfigProvider   
)(implicit ec: ExecutionContext) 
   extends SaleRepository
    with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  implicit val instantColumnType: BaseColumnType[Instant] = MappedColumnType.base[Instant, Timestamp](
    instant => Timestamp.from(instant),
    timestamp => timestamp.toInstant
  )

  private class SalesTable(tag: Tag) extends Table[Sale](tag, "sales") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)    
    def salesamount = column[BigDecimal]("salesamount")    
    def salesdate = column[Instant]("salesdate")  

    def * = (id, salesamount, salesdate).mapTo[Sale]
  }

  private val sales = TableQuery[SalesTable]

  override def salesList(): Future[Seq[Sale]] = {
    db.run(sales.result)
  }
}
