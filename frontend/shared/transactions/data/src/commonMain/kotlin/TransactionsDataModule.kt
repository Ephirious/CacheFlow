import org.koin.dsl.module
import transactions.db.TransactionsDatabaseDataSource
import transactions.repositories.TransactionsRepository
import transactions.repositories.TransactionsRepositoryImpl
import transactions.usecases.GetTransactionsFlowUseCase
import transactions.usecases.UpsertTransactionUseCase

val transactionsDataModule = module {

    single<TransactionsDatabaseDataSource> { TransactionsDatabaseDataSource(get()) }
    single<TransactionsRepository> { TransactionsRepositoryImpl(get(), get()) }


    factory<GetTransactionsFlowUseCase> { GetTransactionsFlowUseCase(get()) }

    factory<UpsertTransactionUseCase> { UpsertTransactionUseCase(get()) }
}