import auth.AuthRepository
import auth.KtorAuthPlugin
import auth.TokenStorage
import auth.cloud.AuthRemoteDataSource
import auth.cloud.KtorAuthPluginImpl
import auth.local.AuthLocalDataSource
import auth.local.TokenStorageImpl
import auth.repositories.AuthRepositoryImpl
import auth.repositories.LogoutDataInternalUseCase
import auth.usecases.GetProfileUseCase
import auth.usecases.LoginUseCase
import auth.usecases.LogoutUseCase
import auth.usecases.RegisterUseCase
import auth.usecases.ResendVerificationCodeUseCase
import auth.usecases.VerifyRegistrationUseCase
import org.koin.dsl.module

val authDataModule = module {
    single<TokenStorage> { TokenStorageImpl(get()) }

    factory<KtorAuthPlugin> { KtorAuthPluginImpl(get(), get()) }

    single<AuthRemoteDataSource> { AuthRemoteDataSource(get(), get()) }
    single<AuthLocalDataSource> { AuthLocalDataSource(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get(), get(), get(), get()) }


    factory { LogoutDataInternalUseCase(get(), get()) }


    factory { LoginUseCase(get(), get()) }
    factory { LogoutUseCase(get()) }
    factory { RegisterUseCase(get()) }
    factory { GetProfileUseCase(get()) }
    factory { ResendVerificationCodeUseCase(get()) }
    factory { VerifyRegistrationUseCase(get(), get()) }
}