// app/security/TOTPqrcodebase64.scala
package security

import javax.inject.Singleton
import java.util.Base64
import dev.samstevens.totp.qr.QrData
import dev.samstevens.totp.qr.ZxingPngQrGenerator
import dev.samstevens.totp.secret.DefaultSecretGenerator

@Singleton
class QrcodeGenerator {

    def generateTotpSecret(): String = {
        val secretGenerator = new DefaultSecretGenerator()
        secretGenerator.generate()        
    }

    def generateQrCode(secret: String, issuer: String, accountName: String): String = {
        val qrData = new QrData.Builder()
            .label(accountName)
            .secret(secret)
            .issuer(issuer)
            .algorithm(dev.samstevens.totp.code.HashingAlgorithm.SHA1)
            .digits(6)
            .period(30)
            .build()

        val generator = new ZxingPngQrGenerator()
        // Force the library to output a 200x200 pixel image
        generator.setImageSize(200) 
        
        val imageData = generator.generate(qrData)
        val base64String = Base64.getEncoder.encodeToString(imageData)
        s"data:image/png;base64,$base64String"
    }
}
