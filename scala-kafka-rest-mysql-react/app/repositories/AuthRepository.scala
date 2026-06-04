// app/repositories/UserRepository.scala
package repositories

import javax.inject.{Inject, Singleton}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.JdbcProfile
import models.User
import java.time.Instant

trait AuthRepository {
  def create(
    firstname: String, 
    lastname: String, 
    email: String, 
    mobile: String, 
    username: String, 
    password: String
  ): Future[User]

  def findByUsername(username: String): Future[Option[User]]  
  def findByEmail(email: String): Future[Option[User]]   
}

@Singleton
class AuthRepositoryImpl @Inject()(
  protected val dbConfigProvider: DatabaseConfigProvider   
)(implicit ec: ExecutionContext) 
  extends AuthRepository 
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

  def create(
    firstname: String, 
    lastname: String, 
    email: String, 
    mobile: String, 
    username: String, 
    password: String
  ): Future[User] = {
    
    val newUserWithoutId = User(
      id = 0, 
      roleId = 2, 
      departmentId = 1, 
      firstname = firstname,
      lastname = lastname,
      email = email,
      mobile = mobile,
      username = username,
      password = password,
      userpic = "pix.png",
      isActive = true,
      isBlocked = false,
      mailtoken = 0,
      secret = None,
      qrcodeurl = None
    )

    val insertQuery = users returning users.map(_.id) into ((user, generatedId) => user.copy(id = generatedId))

    db.run(insertQuery += newUserWithoutId)
  }


  override def findByUsername(username: String): Future[Option[User]] = {
    db.run(users.filter(_.username === username).result.headOption)
  }


  override def findByEmail(email: String): Future[Option[User]] = {
    db.run(users.filter(_.email === email).result.headOption)
  }


}
