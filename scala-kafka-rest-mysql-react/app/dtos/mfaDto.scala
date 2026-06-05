//app/dtos/mfaDto.scala
package dtos

import play.api.libs.json._
import play.api.libs.functional.syntax._


case class ActivatMfaRequest(
  twofactorenabled: Boolean
)

object ActivatMfaRequest {
  implicit val reads: Reads[ActivatMfaRequest] = Json.reads[ActivatMfaRequest]  
}
