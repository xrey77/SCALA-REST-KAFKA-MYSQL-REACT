// app/controllers/MfaController.scala
package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import scala.concurrent.{ExecutionContext, Future}
import services.MfaService
import models.User
import dtos.ActivatMfaRequest

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
//   def varifyOtp(id: Int): Action[JsValue] = Action.async(parse.json) { request =>
//     request.body.validate[UpdatePasswordRequest] match {
//       case JsSuccess(updateRequest, _) =>
//         userService.updateUserPassword(id, updateRequest.password).map {
//           case Some(user) => 
//             Ok(Json.obj("message" -> "Your changed your password successfully."))
//           case None => 
//             NotFound(Json.obj("message" -> "User not found"))
//         }
//       case JsError(errors) =>
//         Future.successful(BadRequest(Json.obj("message" -> "Invalid request")))
//     }
//   }


}