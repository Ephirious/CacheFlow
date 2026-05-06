import editors.db.AccountsDatabaseDataSource
import editors.db.CategoriesDatabaseDataSource
import editors.repositories.AccountsRepository
import editors.repositories.AccountsRepositoryImpl
import editors.repositories.CategoriesRepository
import editors.repositories.CategoriesRepositoryImpl
import editors.usecases.account.CreateAccountUseCase
import editors.usecases.account.DeleteAccountUseCase
import editors.usecases.account.EditAccountUseCase
import editors.usecases.account.GetAccountByIdUseCase
import editors.usecases.account.GetAccountsFlowUseCase
import editors.usecases.category.CreateCategoryUseCase
import editors.usecases.category.DeleteCategoryUseCase
import editors.usecases.category.EditCategoryUseCase
import editors.usecases.category.GetCategoriesFlowUseCase
import editors.usecases.category.GetCategoryByIdUseCase
import org.koin.dsl.module

val editorsDataModule = module {

    single<AccountsDatabaseDataSource> { AccountsDatabaseDataSource(get()) }
    single<CategoriesDatabaseDataSource> { CategoriesDatabaseDataSource(get()) }

    single<AccountsRepository> { AccountsRepositoryImpl(get()) }
    single<CategoriesRepository> { CategoriesRepositoryImpl(get()) }


    factory<GetAccountsFlowUseCase> { GetAccountsFlowUseCase(get()) }
    factory<GetCategoriesFlowUseCase> { GetCategoriesFlowUseCase(get()) }

    factory<GetAccountByIdUseCase> { GetAccountByIdUseCase(get()) }
    factory<GetCategoryByIdUseCase> { GetCategoryByIdUseCase(get()) }

    factory<CreateAccountUseCase> { CreateAccountUseCase(get()) }
    factory<EditAccountUseCase> { EditAccountUseCase(get()) }
    factory<DeleteAccountUseCase> { DeleteAccountUseCase(get()) }

    factory<CreateCategoryUseCase> { CreateCategoryUseCase(get()) }
    factory<EditCategoryUseCase> { EditCategoryUseCase(get()) }
    factory<DeleteCategoryUseCase> { DeleteCategoryUseCase(get()) }
}