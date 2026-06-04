// app/controllers/LoginController.scala
package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import scala.concurrent.{ExecutionContext, Future}
import dtos.LoginUserRequest
import services.AuthService
import scala.concurrent.Future
import security.JwtUtil

@Singleton
class LoginController @Inject()(
  val controllerComponents: ControllerComponents,
  authService: AuthService
)(implicit ec: ExecutionContext) extends BaseController {

    implicit val loginUserRequestFormat: OFormat[LoginUserRequest] = Json.format[LoginUserRequest]


    def signin(): Action[JsValue] = Action.async(parse.json) { request =>
        request.body.validate[LoginUserRequest] match {
            case JsSuccess(dto, _) =>
                authService.getUsername(dto.username).flatMap {
                    case None => 
                        Future.successful(BadRequest(Json.obj("message" -> "Username not found, please register.")))
                    case Some(user) => 
                    
                        val tokenResult = Right(JwtUtil.generateToken(user.id, user.email))

                        authService.signinUser(dto.password, user.password).map { isValid =>
                            if (isValid) {
                                val tokenString = tokenResult.getOrElse("") 
                                Ok(Json.obj(
                                    "message"   -> "You have logged-in successfully, please wait.",
                                    "firstname" -> user.firstname,
                                    "lastname" -> user.lastname,
                                    "email" -> user.email,
                                    "mobile" -> user.mobile,
                                    "username" -> user.username,
                                    "userpic" -> user.userpic,
                                    "isactive" -> user.isActive,
                                    "isblocked" -> user.isBlocked,
                                    "mailtoken" -> user.mailtoken,
                                    "qrcodeurl" -> user.qrcodeurl,
                                    "token" -> tokenString
                                ))
                            } else {
                                BadRequest(Json.obj("message" -> "Invalid Password, please try again."))
                            }
                        }
                }
            case JsError(errors) => 
                Future.successful(BadRequest(Json.obj("message" -> "Invalid JSON format")))
        }
    }



}
