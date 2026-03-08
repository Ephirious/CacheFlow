import interopSample.cloud.InteropSampleRemoteDataSource
import interopSample.local.InteropSampleLocalDataSource
import interopSample.repositories.InteropSampleRepository
import interopSample.repositories.InteropSampleRepositoryImpl
import interopSample.usecases.GetWeatherUseCase
import interopSample.usecases.ManageSampleTextUseCases
import org.koin.dsl.module

val interopSampleDataModule = module {
    single<InteropSampleRemoteDataSource> { InteropSampleRemoteDataSource(get()) }
    single<InteropSampleLocalDataSource> { InteropSampleLocalDataSource(get()) }

    single<InteropSampleRepository> { InteropSampleRepositoryImpl(get(), get()) }

    factory<GetWeatherUseCase> {
        GetWeatherUseCase(get())
    }
    factory<ManageSampleTextUseCases> {
        ManageSampleTextUseCases(get())
    }
}