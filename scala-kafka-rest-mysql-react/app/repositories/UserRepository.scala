// app/repositories/UserRepository.scala
package repositories

import javax.inject.{Inject, Singleton}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.JdbcProfile
import models.User
import models.UserProfile
import java.time.Instant


trait UserRepository {
  def findAll(offset: Int, limit: Int): Future[Seq[User]]
  def findById(id: Int): Future[Option[User]]
  def updateProfile(id: Int, user: UserProfile): Future[UserProfile]
  def delete(id: Int): Future[Boolean]
  def changePassword(id: Int, password: String): Future[User]
}

@Singleton
class UserRepositoryImpl @Inject()(
  protected val dbConfigProvider: DatabaseConfigProvider   
)(implicit ec: ExecutionContext) 
  extends UserRepository 
  with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

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

  override def findAll(offset: Int, limit: Int): Future[Seq[User]] = {
    db.run(users.sortBy(_.id).drop(offset).take(limit).result)
  }

  override def findById(id: Int): Future[Option[User]] = {
    db.run(users.filter(_.id === id).result.headOption)    
  }

  override def updateProfile(id: Int, user: UserProfile): Future[UserProfile] = {
    val query = users
      .filter(_.id === id)
      .map(u => (u.firstname, u.lastname, u.mobile))
      .update((user.firstname, user.lastname, user.mobile))

    db.run(query).map { rowsAffected =>
      if (rowsAffected > 0) user.copy(id = id)
      else throw new Exception(s"User with id $id not found")
    }
  }

  def changePassword(id: Int, password: String): Future[User] = {
    val query = users.filter(_.id === id).map(_.password).update(password)
    
    db.run(query).flatMap { rowsAffected =>
      if (rowsAffected > 0) {
        db.run(users.filter(_.id === id).result.head)
      } else {
        Future.failed(new NoSuchElementException(s"User with id $id not found"))
      }
    }
  }

  override def delete(id: Int): Future[Boolean] = {
    db.run(users.filter(_.id === id).delete).map(_ > 0)
  }
}