// app/security/JwtUtil.scala
package security

import java.time.Clock
import pdi.jwt.{JwtAlgorithm, JwtCirce, JwtClaim}
import io.circe.generic.auto._
import io.circe.syntax._
import com.typesafe.config.ConfigFactory
import scala.util.{Success, Failure, Try}

case class TokenContent(userId: Long, email: String)

object JwtUtil {
  private val config = ConfigFactory.load()
  private val secretKey = Try(config.getString("jwt.secret")).getOrElse("default_safe_secret_key")
  private val expireInSeconds = Try(config.getLong("jwt.expireInSeconds")).getOrElse(3600L)

  private val algorithm = JwtAlgorithm.HS256
  implicit val clock: Clock = Clock.systemUTC()



  // private val config = ConfigFactory.load()
  // private val secretKey = config.getString("jwt.secret")
  // private val expireInSeconds = config.getLong("jwt.expireInSeconds")
  // private val algorithm = JwtAlgorithm.HS256  
  // implicit val clock: Clock = Clock.systemUTC()

  def generateToken(userId: Long, email: String): String = {
    val content = TokenContent(userId, email).asJson.noSpaces
    val claim = JwtClaim(content)
      .issuedNow
      .expiresIn(expireInSeconds)
      
    JwtCirce.encode(claim, secretKey, algorithm)
  }

  def verifyToken(token: String): Try[TokenContent] = {
    JwtCirce.decodeJson(token, secretKey, Seq(algorithm)).flatMap { claimJson =>
      claimJson.as[TokenContent].toTry
    }
  }
}
