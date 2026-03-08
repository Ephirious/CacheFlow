import interopSample.ktor.InteropSampleRemoteDataSource
import interopSample.repositories.InteropSampleRepository
import interopSample.repositories.InteropSampleRepositoryImpl
import interopSample.usecases.GetWeatherUseCase
import org.koin.dsl.module

val interopSampleDataModule = module {
    single<InteropSampleRemoteDataSource> { InteropSampleRemoteDataSource(get()) }

    single<InteropSampleRepository> { InteropSampleRepositoryImpl(get()) }

    factory<GetWeatherUseCase> {
        GetWeatherUseCase(get())
    }
}