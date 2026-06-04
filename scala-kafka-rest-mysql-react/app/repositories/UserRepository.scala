// app/repositories/UserRepository.scala
// package repositories

// import javax.inject._
// import scala.concurrent.{ExecutionContext, Future}
// import play.api.db.slick.DatabaseConfigProvider
// import slick.jdbc.JdbcProfile
// import models.{User, UserRole}
// import java.time.Instant

// trait UserRepository {
//   def findAll(offset: Int, limit: Int): Future[Seq[User]]
//   def findById(id: Long): Future[Option[User]]
//   def update(user: User): Future[User]
//   def delete(id: Long): Future[Boolean]
// }

// @Singleton
// class UserRepositoryImpl @Inject()(
//   dbConfigProvider: DatabaseConfigProvider
// )(implicit ec: ExecutionContext) extends UserRepository {

//   private val dbConfig = dbConfigProvider.get[JdbcProfile]
//   import dbConfig._
//   import profile.api._

//   class UsersTable(tag: Tag) extends Table[User](tag, "users") {
//     def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
//     def firstname = column[String]("firstname")
//     def lastname = column[String]("lastname")
//     def email = column[String]("email", O.Unique)
//     def mobile = column[String]("mobile")
//     def username = column[String]("username")
//     def passwordHash = column[String]("password_hash")

//     def * = (id, firstname, lastname, email, mobile, username, password).mapTo[User]

    // def role = column[String]("role")
    // def createdAt = column[Instant]("created_at")
    // def updatedAt = column[Instant]("updated_at")
    // def * = (id, email, name, role, createdAt, updatedAt).mapTo[User]
  // }

  // private val users = TableQuery[UsersTable]

//   override def findAll(offset: Int, limit: Int): Future[Seq[User]] = {
//     db.run(users.sortBy(_.createdAt.desc).drop(offset).take(limit).result)
//   }

//   override def findById(id: Long): Future[Option[User]] = {
//     db.run(users.filter(_.id === id).result.headOption)
//   }

//   override def update(user: User): Future[User] = {
//     val now = Instant.now()
//     val query = users.filter(_.id === user.id)
//       .map(u => (u.email, u.name, u.role, u.updatedAt))
//       .update((user.email, user.name, user.role.toString, now))
//     db.run(query).map(_ => user.copy(updatedAt = now))
//   }

//   override def delete(id: Long): Future[Boolean] = {
//     db.run(users.filter(_.id === id).delete).map(_ > 0)
//   }
// }