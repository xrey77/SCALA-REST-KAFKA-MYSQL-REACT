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

    bind(classOf[ProductService]).to(classOf[ProductServiceImpl])
    bind(classOf[ProductRepository]).to(classOf[ProductRepositoryImpl])

    bind(classOf[MfaService]).to(classOf[MfaServiceImpl])
    bind(classOf[MfaRepository]).to(classOf[MfaRepositoryImpl])

    bind(classOf[SaleService]).to(classOf[SaleServiceImpl])
    bind(classOf[SaleRepository]).to(classOf[SaleRepositoryImpl])

    bind(classOf[PasswordHasher]).to(classOf[BCryptPasswordHasher])    
    bind(classOf[QrcodeGenerator]).asEagerSingleton()


  }
}