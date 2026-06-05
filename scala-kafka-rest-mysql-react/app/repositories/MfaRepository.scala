// app/repositories/MfaRepository.scala
package repositories

import javax.inject.{Inject, Singleton}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.JdbcProfile
import models.User
import java.time.Instant


trait MfaRepository {
    def findUserById(id: Int): Future[Option[User]]
    def activateMfa(id: Int, secret: String, qrcode: String): Future[Option[User]]
}

@Singleton
class MfaRepositoryImpl @Inject()(
  protected val dbConfigProvider: DatabaseConfigProvider   
)(implicit ec: ExecutionContext) 
  extends MfaRepository 
  with HasDatabaseConfigProvider[JdbcProfile] {

    import dbConfig.profile.api._ 

    class UsersTable(tag: Tag) extends Table[User](tag, "users") {
        def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
        def roleId = column[Int]("role_id")    
        def departmentId = column[Int]("department_id")    
        def firstname = column[String]("firstname")
        def lastname = column[String]("lastname")
        def email = column[String]("email")
        def mobile = column[String]("mobile")
        def username = column[String]("username")
        def password = column[String]("password")
        def userpic = column[String]("userpic", O.Default("pix.png"))    
        def isActive = column[Boolean]("isactive")
        def isBlocked = column[Boolean]("isblocked")
        def mailtoken = column[Int]("mailtoken")
        def secret = column[Option[String]]("secret")
        def qrcodeurl = column[Option[String]]("qrcodeurl")
        
        def * = (id, roleId, departmentId, firstname, lastname, email, mobile, username, password, userpic, isActive, isBlocked, mailtoken, secret, qrcodeurl).mapTo[User]
    }

    private val users = TableQuery[UsersTable]

    def activateMfa(id: Int, secret: String, qrcodeurl: String): Future[Option[User]] = {
        val query = users.filter(_.id === id).map(u => (u.secret, u.qrcodeurl))
        
        val updateAction = query.update((Some(secret), Some(qrcodeurl)))
        // db.run(updateAction).map { rowsUpdated =>
        //     if (rowsUpdated > 0) Some(qrcodeurl) else None
        // }
        db.run(updateAction).flatMap { rowsUpdated =>
            if (rowsUpdated > 0) {
                db.run(users.filter(_.id === id).result.headOption)
            } else {
                Future.successful(None)
            }
        }
    }

    // def verifyOtp(
    //     id: Int, 
    //     oto: String
    // ): Future[User] = {
    //     db.run(query).map { rowsAffected =>
    //     if (rowsAffected > 0) user.copy(id = id)
    //     else throw new Exception(s"User with id $id not found")        
        
    // }


    override def findUserById(id: Int): Future[Option[User]] = {
        db.run(users.filter(_.id === id).result.headOption)    
    }

  
}
