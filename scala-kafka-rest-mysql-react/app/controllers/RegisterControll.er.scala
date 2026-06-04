// app/controllers/RegisterController.scala
package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import scala.concurrent.{ExecutionContext, Future}
import dtos.RegisterUserRequest
import services.AuthService

@Singleton
class RegisterController @Inject()(
  val controllerComponents: ControllerComponents,
  authService: AuthService
)(implicit ec: ExecutionContext) extends BaseController {

    implicit val registerUserRequestFormat: OFormat[RegisterUserRequest] = Json.format[RegisterUserRequest]

    def create(): Action[JsValue] = Action.async(parse.json) { request =>
        request.body.validate[RegisterUserRequest] match {
            case JsSuccess(dto, _) =>

              for {
                  emailExists <- authService.getEmailAddress(dto.email).map(_.isDefined)
                  usernameExists <- authService.getUsername(dto.username).map(_.isDefined)
                  result <- (emailExists, usernameExists) match {
                      case (true, _) => 
                          Future.successful(BadRequest(Json.obj("message" -> "Email Address is already taken.")))
                      case (_, true) => 
                          Future.successful(BadRequest(Json.obj("message" -> "Username is already taken.")))
                      case (false, false) =>

                        authService.createUser(dto).map { user =>

                            val responseJson = Json.obj(
                              "message" -> "You have registered successfully, please login now."
                            )
                        
                            Created(responseJson)
                                  .withHeaders("Location" -> s"/api/v1/users/${user.id}")
                        }
                  }
              } yield result


            case JsError(errors) =>
                val errorJson = Json.obj(
                  "status" -> "error",
                  "message" -> "Invalid JSON data provided",
                  "details" -> JsError.toJson(errors)
                )
                Future.successful(BadRequest(errorJson))
        }
    }
}
