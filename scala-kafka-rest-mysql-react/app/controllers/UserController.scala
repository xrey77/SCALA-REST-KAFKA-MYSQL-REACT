// app/controllers/UserController.scala
package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import scala.concurrent.{ExecutionContext, Future}
import services.UserService
import models.User
import dtos.UpdateProfileRequest
import dtos.UpdatePasswordRequest

import play.api.Environment
import java.io.File
import java.nio.file.Paths
import org.apache.commons.io.FilenameUtils

import security.{AuthenticatedAction, TokenContent}

@Singleton
class UserController @Inject()(
  val controllerComponents: ControllerComponents,
  authAction: AuthenticatedAction,
  userService: UserService,
  env: Environment
)(implicit ec: ExecutionContext) extends BaseController {

  // GET /api/v1/getusers/1/10
  def list(page: Int, size: Int): Action[AnyContent] = Action.async {
    if (page < 1 || size < 1 || size > 100) {
      Future.successful(BadRequest(Json.obj(
        "message" -> "Invalid pagination parameters"
      )))
    } else {
      userService.listUsers(page, size).map { users =>
        Ok(Json.toJson(users))
      }
    }
  }

  // GET /api/v1/getuserbyid/:id
  def get(id: Int): Action[AnyContent] = authAction.async {
    userService.getUserid(id).map {
      case Some(user) => Ok(Json.toJson(user))
      case None => NotFound(Json.obj("message" -> "User not found", "id" -> id))
    }
  }


  // PATCH /api/v1/updateuser/:id
  def updateuser(id: Int): Action[JsValue] = authAction.async (parse.json) { request =>
    request.body.validate[UpdateProfileRequest] match {
      case JsSuccess(updateRequest, _) =>
        userService.updateUserProfile(id, updateRequest).map {
          case Some(user) => 
            Ok(Json.obj("message" -> "Your profile has been updated successfully."))
          case None => 
            NotFound(Json.obj("message" -> "User not found"))
        }
      case JsError(errors) =>
        Future.successful(BadRequest(Json.obj("message" -> "Invalid request")))
    }
  }


  // PATCH /api/v1/changepassword/:id
  def updatePassword(id: Int): Action[JsValue] = authAction.async (parse.json) { request =>
    request.body.validate[UpdatePasswordRequest] match {
      case JsSuccess(updateRequest, _) =>
        userService.updateUserPassword(id, updateRequest.password).map {
          case Some(user) => 
            Ok(Json.obj("message" -> "Your changed your password successfully."))
          case None => 
            NotFound(Json.obj("message" -> "User not found"))
        }
      case JsError(errors) =>
        Future.successful(BadRequest(Json.obj("message" -> "Invalid request")))
    }
  }


//   // DELETE /api/v1/deluser/:id
  def delete(id: Int): Action[AnyContent] = authAction.async {
    userService.deleteUser(id).map {
      case true  => Ok(Json.obj("message" -> s"User ID: $id has been deleted successfully."))
      case false => NotFound(Json.obj("message" -> "User not found"))
    }
  }

  def uploadImage(id: Int): Action[MultipartFormData[play.api.libs.Files.TemporaryFile]] = authAction.async(parse.multipartFormData) { request =>
    request.body.file("userpic") match {
      case Some(imageFile) =>
        val filename = Paths.get(imageFile.filename).getFileName.toString
        val extension = FilenameUtils.getExtension(filename)
        val targetDirectory = new File(env.rootPath, "public/users")
        if (!targetDirectory.exists()) targetDirectory.mkdirs()

        val newFilename = s"00${id}.${extension}" 
        val destination = new File(targetDirectory, newFilename)
        imageFile.ref.copyTo(destination, replace = true)
        userService.updateProfilePicture(id, newFilename)
        Future.successful(
          Ok(Json.obj("message" -> "You have changed your profile picture successfully.", "userpic" -> newFilename))
        )

      case None =>
        Future.successful(
          BadRequest("Missing file with key 'image'")
        )        
    }
  }

}