// app/security/PasswordHasher.scala
package security

import com.google.inject.ImplementedBy

@ImplementedBy(classOf[BCryptPasswordHasher])
trait PasswordHasher {
  def hash(password: String): String
  def check(password: String, hash: String): Boolean
}
