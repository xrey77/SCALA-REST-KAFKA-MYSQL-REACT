// app/services/MfaService.scala
package services

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import models.User
import models.MfaActivationResult
import repositories.MfaRepositoryImpl
import security.QrcodeGenerator

trait MfaService {
    def mfaActivation(id: Int, twofactorenabled: Boolean): Future[Option[MfaActivationResult]]    
}

@Singleton
class MfaServiceImpl @Inject()(
  mfaRepository: MfaRepositoryImpl,
  qrcodeGenerator: QrcodeGenerator
)(implicit ec: ExecutionContext) extends MfaService {


    override def mfaActivation(id: Int, twofactorenabled: Boolean): Future[Option[MfaActivationResult]] = {
        mfaRepository.findUserById(id).flatMap {
            case Some(existing) =>
                if (twofactorenabled) {
                    val secretKey = qrcodeGenerator.generateTotpSecret()
                    val qrCodeUrl = qrcodeGenerator.generateQrCode(secretKey, "BANK OF AMERICA", existing.email)

                    mfaRepository.activateMfa(id, secretKey, qrCodeUrl).map {
                        case Some(updatedUser) => Some(MfaActivationResult(updatedUser, Some(qrCodeUrl)))
                        case None              => None
                    }
                    
                } else {
                    mfaRepository.activateMfa(id, "", "").map {
                        case Some(updatedUser) => Some(MfaActivationResult(updatedUser, None))
                        case None              => None
                    }
                }

            case None => 
                Future.successful(None)
        }
    }

    
    // override def mfaActivation(id: Int, twofactorenabled: Boolean): Future[Option[User]] = {
    //     mfaRepository.findUserById(id).flatMap {
    //     case Some(existing) =>
    //         if (twofactorenabled) {
    //             val secretKey = qrcodeGenerator.generateTotpSecret()
    //             val qrCodeUrl = qrcodeGenerator.generateQrCode(secretKey, "BANK OF AMERICA", existing.email)

    //             mfaRepository.activateMfa(id, secretKey, qrCodeUrl)
    //         } else {
    //             mfaRepository.activateMfa(id, "", "").map(Some(_))
    //         }

    //     case None => 
    //         Future.successful(None)
    //     }

    // }

}