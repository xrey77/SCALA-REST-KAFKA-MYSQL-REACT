// app/security/AuthenticatedAction.scala
package security

import javax.inject.Inject
import play.api.mvc._
import play.api.libs.json.Json
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Success, Failure}

class UserRequest[A](val tokenContent: TokenContent, request: Request[A]) extends WrappedRequest[A](request)

class AuthenticatedAction @Inject()(val parser: BodyParsers.Default)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[UserRequest, AnyContent] {

  override def invokeBlock[A](request: Request[A], block: UserRequest[A] => Future[Result]): Future[Result] = {
    val maybeToken = request.headers.get("Authorization").flatMap { authHeader =>
      if (authHeader.startsWith("Bearer ")) Some(authHeader.substring(7)) else None
    }

    maybeToken match {
      case Some(token) =>
        JwtUtil.verifyToken(token) match {
          case Success(tokenContent) => 
            block(new UserRequest(tokenContent, request))
          case Failure(_) => 
            Future.successful(Results.Unauthorized(Json.obj("message" -> "Invalid or expired token")))
        }
      case None => 
        Future.successful(Results.Unauthorized(Json.obj("message" -> "UnAuthorizaed Access!")))
    }
  }
}
