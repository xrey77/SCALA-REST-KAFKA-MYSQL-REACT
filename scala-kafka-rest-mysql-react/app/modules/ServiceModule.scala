// app/modules/ServiceModule.scala
package modules

import com.google.inject.AbstractModule
import play.api.{Configuration, Environment}
import services._
import repositories._
import security.{PasswordHasher, BCryptPasswordHasher, QrcodeGenerator}

class ServiceModule(environment: Environment, configuration: Configuration) extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[AuthService]).to(classOf[AuthServiceImpl])
    bind(classOf[AuthRepository]).to(classOf[AuthRepositoryImpl])

    bind(classOf[UserService]).to(classOf[UserServiceImpl])
    bind(classOf[UserRepository]).to(classOf[UserRepositoryImpl])

    bind(classOf[MfaService]).to(classOf[MfaServiceImpl])
    bind(classOf[MfaRepository]).to(classOf[MfaRepositoryImpl])

    bind(classOf[PasswordHasher]).to(classOf[BCryptPasswordHasher])    
    // bind(classOf[QrcodeGenerator]).to(classOf[QrcodeGeneratorImpl])
    bind(classOf[QrcodeGenerator]).asEagerSingleton()


  }
}