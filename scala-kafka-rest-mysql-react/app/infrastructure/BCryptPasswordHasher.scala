// app/infrastructure/BCryptPasswordHasher.scala
package infrastructure

import security.PasswordHasher
import org.mindrot.jbcrypt.BCrypt

class BCryptPasswordHasher extends PasswordHasher {
  override def hash(password: String): String = 
    BCrypt.hashpw(password, BCrypt.gensalt(12))

  override def check(password: String, hash: String): Boolean = 
    BCrypt.checkpw(password, hash)
}
