import org.koin.dsl.module
import transactions.db.TransactionsDatabaseDataSource
import transactions.local.TransactionsLocalDataSource
import transactions.repositories.TransactionsRepository
import transactions.repositories.TransactionsRepositoryImpl
import transactions.usecases.DeleteTransactionUseCase
import transactions.usecases.GetTransactionUseCase
import transactions.usecases.GetTransactionsFlowUseCase
import transactions.usecases.UpsertTransactionUseCase

val transactionsDataModule = module {

    single<TransactionsDatabaseDataSource> { TransactionsDatabaseDataSource(get(), get(), get(), get()) }
    single<TransactionsLocalDataSource> { TransactionsLocalDataSource(get()) }
    single<TransactionsRepository> { TransactionsRepositoryImpl(get(), get()) }


    factory<GetTransactionsFlowUseCase> { GetTransactionsFlowUseCase(get()) }

    factory<UpsertTransactionUseCase> { UpsertTransactionUseCase(get()) }

    factory<GetTransactionUseCase> { GetTransactionUseCase(get()) }
    factory<DeleteTransactionUseCase> { DeleteTransactionUseCase(get()) }
}