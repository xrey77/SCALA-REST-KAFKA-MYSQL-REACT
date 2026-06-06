// app/controllers/MfaController.scala
package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import scala.concurrent.{ExecutionContext, Future}
import services.MfaService
import models.User
import dtos.ActivatMfaRequest
import dtos.MfaDtoRequest
import models.UserNotFound
import models.MfaNotConfigured
import models.InvalidOtpCode
import models.OtpExpired

@Singleton
class MfaController @Inject()(
  val controllerComponents: ControllerComponents,
  mfaService: MfaService
)(implicit ec: ExecutionContext) extends BaseController {

  // PATCH /api/v1/mfaactivation/:id
  def activatemfa(id: Int): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[ActivatMfaRequest] match {
      case JsSuccess(updateRequest, _) =>
            mfaService.mfaActivation(id, updateRequest.twofactorenabled).map {
            case Some(user) => 
                val msg = if (updateRequest.twofactorenabled) "MFA is enabled successfully." else "MFA is disabled successfully."
                Ok(Json.obj("message" -> msg))            
            case None => 
                NotFound(Json.obj("message" -> "User not found"))
            }
      case JsError(errors) =>
        Future.successful(BadRequest(Json.obj("message" -> "Invalid request")))
    }
  }


  // PATCH /api/v1/verifytotp/:id
  def varifyOtp(id: Int): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[MfaDtoRequest] match {
      case JsSuccess(updateRequest, _) =>

        mfaService.validateOtp(id, updateRequest.otp).map {
          case Right(user) => 
            Ok(Json.obj("message" -> "OTP code has been validated successfully."))
          case Left(UserNotFound) => 
            NotFound(Json.obj("error" -> "User not found"))
          case Left(MfaNotConfigured) => 
            BadRequest(Json.obj("error" -> "MFA is not yet activated."))
          case Left(InvalidOtpCode) => 
            Unauthorized(Json.obj("error" -> "Invalid OTP code, please try again."))
          case Left(OtpExpired) => 
            BadRequest(Json.obj("error" -> "OTP code has expired, please request a new one."))
        }
        
      case JsError(errors) =>
        Future.successful(BadRequest(JsError.toJson(errors)))
    }
  }  
  // def varifyOtp(id: Int): Action[JsValue] = Action.async(parse.json) { request =>
  //   request.body.validate[MfaDtoRequest] match {
  //     case JsSuccess(updateRequest, _) =>
  //       mfaService.validateOtp(id, updateRequest.otp).map {
  //         case Some(user) => 
  //           Ok(Json.obj("message" -> "OTP code has been validated successfully."))
  //         case None => 
  //           NotFound(Json.obj("message" -> "User not found"))
  //       }
  //     case JsError(errors) =>
  //       Future.successful(BadRequest(Json.obj("message" -> "Invalid request")))
  //   }
  // }


}