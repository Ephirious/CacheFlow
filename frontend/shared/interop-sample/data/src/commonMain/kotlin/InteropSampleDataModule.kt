import interopSample.cloud.InteropSampleRemoteDataSource
import interopSample.db.InteropSampleDatabaseDataSource
import interopSample.local.InteropSampleLocalDataSource
import interopSample.repositories.InteropSampleRepository
import interopSample.repositories.InteropSampleRepositoryImpl
import interopSample.usecases.GetWeatherFlowUseCase
import interopSample.usecases.GetWeatherUseCase
import interopSample.usecases.ManageSampleTextUseCases
import interopSample.usecases.RefreshWeatherUseCase
import org.koin.dsl.module

val interopSampleDataModule = module {
    single<InteropSampleRemoteDataSource> { InteropSampleRemoteDataSource(get()) }
    single<InteropSampleLocalDataSource> { InteropSampleLocalDataSource(get()) }
    single<InteropSampleDatabaseDataSource> { InteropSampleDatabaseDataSource(get()) }

    single<InteropSampleRepository> { InteropSampleRepositoryImpl(get(), get(), get()) }

    factory<GetWeatherFlowUseCase> {
        GetWeatherFlowUseCase(get())
    }
    factory<GetWeatherUseCase> {
        GetWeatherUseCase(get())
    }
    factory<RefreshWeatherUseCase> {
        RefreshWeatherUseCase(get())
    }
    factory<ManageSampleTextUseCases> {
        ManageSampleTextUseCases(get())
    }

}