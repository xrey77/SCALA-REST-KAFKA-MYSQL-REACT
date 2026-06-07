// app/services/AuthService.scala
package services

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import dtos.RegisterUserRequest
import models.User
import repositories.AuthRepositoryImpl
import security.BCryptPasswordHasher
import services.KafkaProducerService
import scala.util.{Success, Failure}

trait AuthService {
  def createUser(request: RegisterUserRequest): Future[User]
  def getUsername(username: String): Future[Option[User]]
  def getEmailAddress(email: String): Future[Option[User]]  
  def signinUser(usreId: String, plain_password: String, hash_password: String): Future[Boolean]  
}

@Singleton
class AuthServiceImpl @Inject()(
  authRepository: AuthRepositoryImpl,
  passwordService: BCryptPasswordHasher,
  kafkaProducer: KafkaProducerService
)(implicit ec: ExecutionContext) extends AuthService {

  override def createUser(request: RegisterUserRequest): Future[User] = {
    val hashedPassword = passwordService.hash(request.password)
    
    authRepository.create(
      request.firstname,
      request.lastname,
      request.email,
      request.mobile,
      request.username,
      hashedPassword
    ).map { user =>


    kafkaProducer.sendToCentralTopic(user.id.toString(), "register_event").onComplete {
      case Success(metadata) =>
        println(s"Ack received! Topic: ${metadata.topic()}, Partition: ${metadata.partition()}, Offset: ${metadata.offset()}")
      case Failure(exception) =>
        println(s"Delivery failed: ${exception.getMessage}")
    }

      user
    }
  }

  override def getUsername(username: String): Future[Option[User]] = {
    authRepository.findByUsername(username) 
  }

  override def getEmailAddress(email: String): Future[Option[User]] = {
    authRepository.findByEmail(email)
  }

  override def signinUser(userId: String, plain_password: String, hash_password: String): Future[Boolean] = {
    passwordService.check(plain_password, hash_password)    


    kafkaProducer.sendToCentralTopic(userId, "login_event").onComplete {
      case Success(metadata) =>
        println(s"Ack received! Topic: ${metadata.topic()}, Partition: ${metadata.partition()}, Offset: ${metadata.offset()}")
      case Failure(exception) =>
        println(s"Delivery failed: ${exception.getMessage}")
    }


    Future.successful(true)
  }
}