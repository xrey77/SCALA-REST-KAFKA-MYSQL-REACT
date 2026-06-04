// app/security/BCryptPasswordHasher.scala
package security

import javax.inject.Singleton
import org.mindrot.jbcrypt.BCrypt

@Singleton
class BCryptPasswordHasher extends PasswordHasher {
  
  override def hash(password: String): String = {
    BCrypt.hashpw(password, BCrypt.gensalt())    
  }

  override def check(password: String, hash: String): Boolean = {
    BCrypt.checkpw(password, hash)
  }
}
