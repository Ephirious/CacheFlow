package core.sqldelight

import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.async.coroutines.awaitCreate
import app.cash.sqldelight.db.SqlDriver
import data.*
import org.cacheflow.db.Database
import org.koin.core.module.Module
import org.koin.dsl.module

suspend fun createDriver(isSW: Boolean): SqlDriver {
    val schema = Database.Schema
    val driver = SqlDriverFactory().createDriver(schema, "app_db", isSW)
    schema.awaitCreate(driver)
    return driver
}

suspend fun getSqlDriverModule(isSW: Boolean): Module {
    val driver = createDriver(isSW)
    return module {
        single<SqlDriver> {
            driver
        }
    }
}

internal val sqlModule = module {
    // SqlDriver creating in initKoin!!

    single<Database> {
        Database(
            driver = get<SqlDriver>(),
            AccountsAdapter = Accounts.Adapter(
                fundsAdapter = bigDecimalAdapter,
                created_atAdapter = instantAdapter,
                updated_atAdapter = instantAdapter
            ),
            CategoriesAdapter = Categories.Adapter(
                created_atAdapter = instantAdapter,
                updated_atAdapter = instantAdapter,
                typeAdapter = EnumColumnAdapter(),
            ),
            OperationsAdapter = Operations.Adapter(
                amountAdapter = bigDecimalAdapter,
                dateAdapter = instantAdapter,
                created_atAdapter = instantAdapter,
                updated_atAdapter = instantAdapter
            ),
            TransfersAdapter = Transfers.Adapter(
                created_atAdapter = instantAdapter,
                updated_atAdapter = instantAdapter
            ),
            SyncOperationsAdapter = SyncOperations.Adapter(
                actionAdapter = EnumColumnAdapter(),
                table_typeAdapter = EnumColumnAdapter()
            )
        )
    }

    single<AccountsQueries> { get<Database>().accountsQueries }
    single<CategoriesQueries> { get<Database>().categoriesQueries }
    single<OperationsQueries> { get<Database>().operationsQueries }
    single<TransfersQueries> { get<Database>().transfersQueries }
    single<CommonQueries> { get<Database>().commonQueries }

    single<SyncDBQueries> { get<Database>().syncDBQueries }
    single<SyncInternalQueries> { get<Database>().syncInternalQueries }

    single<TriggersQueries> { get<Database>().triggersQueries }
}