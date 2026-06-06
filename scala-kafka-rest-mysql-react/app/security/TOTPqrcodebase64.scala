// app/security/TOTPqrcodebase64.scala
package security

import javax.inject.Singleton
import java.util.Base64
import dev.samstevens.totp.qr.QrData
import dev.samstevens.totp.qr.ZxingPngQrGenerator
import dev.samstevens.totp.secret.DefaultSecretGenerator

import dev.samstevens.totp.time.SystemTimeProvider
import dev.samstevens.totp.code.DefaultCodeGenerator
import dev.samstevens.totp.code.DefaultCodeVerifier

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
        generator.setImageSize(200) 
        
        val imageData = generator.generate(qrData)
        val base64String = Base64.getEncoder.encodeToString(imageData)
        s"data:image/png;base64,$base64String"
    }

    def verifyCode(secret: String, code: String): Boolean = {
        val timeProvider = new SystemTimeProvider()
        val codeGenerator = new DefaultCodeGenerator()
        
        val verifier = new DefaultCodeVerifier(codeGenerator, timeProvider)
        
        // Configures the allowable clock drift (+/- 1 time period)
        verifier.setAllowedTimePeriodDiscrepancy(1)         
        
        // Remember to supply both parameters to isValidCode
        verifier.isValidCode(secret, code)
    }

}
