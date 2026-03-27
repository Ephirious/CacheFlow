import editors.db.AccountsDatabaseDataSource
import editors.db.CategoriesDatabaseDataSource
import editors.repositories.AccountsRepository
import editors.repositories.AccountsRepositoryImpl
import editors.repositories.CategoriesRepository
import editors.repositories.CategoriesRepositoryImpl
import editors.usecases.account.GetAccountsFlowUseCase
import editors.usecases.category.GetCategoriesFlowUseCase
import org.koin.dsl.module

val editorsDataModule = module {

    single<AccountsDatabaseDataSource> { AccountsDatabaseDataSource(get()) }
    single<CategoriesDatabaseDataSource> { CategoriesDatabaseDataSource(get()) }

    single<AccountsRepository> { AccountsRepositoryImpl(get(), get()) }
    single<CategoriesRepository> { CategoriesRepositoryImpl(get()) }


    factory<GetAccountsFlowUseCase> { GetAccountsFlowUseCase(get()) }
    factory<GetCategoriesFlowUseCase> { GetCategoriesFlowUseCase(get()) }
}