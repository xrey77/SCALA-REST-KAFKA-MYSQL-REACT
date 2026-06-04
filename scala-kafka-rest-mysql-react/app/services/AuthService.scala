// app/services/AuthService.scala
package services

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import dtos.RegisterUserRequest
import models.User
import repositories.AuthRepositoryImpl
import security.PasswordHasher

trait AuthService {
  def createUser(request: RegisterUserRequest): Future[User]
  def getUsername(username: String): Future[Option[User]]
  def getEmailAddress(email: String): Future[Option[User]]  
  def signinUser(plain_password: String, hash_password: String): Future[Boolean]   
}

@Singleton
class AuthServiceImpl @Inject()(
  authRepository: AuthRepositoryImpl,
  passwordService: PasswordHasher
)(implicit ec: ExecutionContext) extends AuthService {

  override def createUser(request: RegisterUserRequest): Future[User] = {
    for {
      hashedPassword <- Future.successful(passwordService.hash(request.password))      
      user <- authRepository.create(
        request.firstname,
        request.lastname,
        request.email,
        request.mobile,
        request.username,
        hashedPassword
      )
    } yield user
  }


 override def getUsername(username: String): Future[Option[User]] = {
    authRepository.findByUsername(username)
  }

  override def getEmailAddress(email: String): Future[Option[User]] = {
    authRepository.findByEmail(email)
  }

  override def signinUser(plain_password: String, hash_password: String): Future[Boolean] = {    
    Future.successful(passwordService.check(plain_password, hash_password))    
  }

}