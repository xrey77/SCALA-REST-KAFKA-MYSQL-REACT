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

@Singleton
class UserController @Inject()(
  val controllerComponents: ControllerComponents,
  userService: UserService
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
  def get(id: Int): Action[AnyContent] = Action.async {
    userService.getUserid(id).map {
      case Some(user) => Ok(Json.toJson(user))
      case None => NotFound(Json.obj("message" -> "User not found", "id" -> id))
    }
  }


  // PATCH /api/v1/updateuser/:id
  def updateuser(id: Int): Action[JsValue] = Action.async(parse.json) { request =>
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
  def updatePassword(id: Int): Action[JsValue] = Action.async(parse.json) { request =>
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
  def delete(id: Int): Action[AnyContent] = Action.async {
    userService.deleteUser(id).map {
      case true => NoContent
      case false => NotFound(Json.obj("message" -> "User not found"))
    }
  }
}