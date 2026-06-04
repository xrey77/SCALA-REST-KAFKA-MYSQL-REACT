// app/modules/ServiceModule.scala
package modules

import com.google.inject.AbstractModule
import play.api.{Configuration, Environment}
import services._
import repositories._
import security.{PasswordHasher, BCryptPasswordHasher}

class ServiceModule(environment: Environment, configuration: Configuration) extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[AuthService]).to(classOf[AuthServiceImpl])
    bind(classOf[AuthRepository]).to(classOf[AuthRepositoryImpl])
    bind(classOf[PasswordHasher]).to(classOf[BCryptPasswordHasher])    
    // bind(classOf[PasswordService]).to(classOf[BCryptPasswordService])
  }
}