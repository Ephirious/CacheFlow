import editors.db.AccountsDatabaseDataSource
import editors.repositories.AccountsRepository
import editors.repositories.AccountsRepositoryImpl
import editors.usecases.account.GetAccountsFlowUseCase
import org.koin.dsl.module

val editorsDataModule = module {

    single<AccountsDatabaseDataSource> { AccountsDatabaseDataSource(get()) }

    single<AccountsRepository> { AccountsRepositoryImpl(get(), get()) }


    factory<GetAccountsFlowUseCase> { GetAccountsFlowUseCase(get()) }
}