// app/services/UserService.scala
package services

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import models.User
import models.UserProfile
import models.UserProfilepic
import dtos.UpdateProfileRequest
import repositories.UserRepository
import security.BCryptPasswordHasher

trait UserService {
  def listUsers(page: Int, size: Int): Future[Seq[User]]
  def getUserid(id: Int): Future[Option[User]]
  def updateUserProfile(id: Int, request: UpdateProfileRequest): Future[Option[UserProfile]]
  def updateUserPassword(id: Int, password: String): Future[Option[User]]
  def deleteUser(id: Int): Future[Boolean]
  def updateProfilePicture(id: Int, userpic: String): Future[Option[UserProfilepic]]
}

@Singleton
class UserServiceImpl @Inject()(
  userRepository: UserRepository,
  passwordService: BCryptPasswordHasher
)(implicit ec: ExecutionContext) extends UserService {

  override def listUsers(page: Int, size: Int): Future[Seq[User]] = {
    val offset = (page - 1) * size
    userRepository.findAll(offset, size)
  }

  override def getUserid(id: Int): Future[Option[User]] = {
    userRepository.findById(id)
  }

  override def updateUserProfile(id: Int, request: UpdateProfileRequest): Future[Option[UserProfile]] = {
    userRepository.findById(id).flatMap {
      case Some(existing) =>
        val updated = UserProfile(
          id = id, firstname = request.firstname, lastname = request.lastname, mobile = request.mobile
        )
        userRepository.updateProfile(id, updated).map(Some(_))        
      case None => Future.successful(None)
    }
  }

  override def updateUserPassword(id: Int, password: String): Future[Option[User]] = {
    val hashedPassword = passwordService.hash(password)          
    userRepository.findById(id).flatMap {
      case Some(existing) =>
        userRepository.changePassword(id, hashedPassword).map(Some(_))        
      case None => Future.successful(None)
    }
  }

  override def deleteUser(id: Int): Future[Boolean] = {
    userRepository.delete(id)
  }

  override def updateProfilePicture(id: Int, userpic: String): Future[Option[UserProfilepic]] = {
    userRepository.findById(id).flatMap {
      case Some(existing) =>
        userRepository.updateUserpic(id, userpic).map(Some(_))        
      case None => Future.successful(None)
    }
  }



}