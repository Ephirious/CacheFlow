package manageTransaction.modals


import editors.accounts.CreateAccountComponent
import editors.categories.CreateCategoryComponent
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@Serializable
sealed class ManageTransactionModalConfig {
    @Serializable
    data object CreateCategory : ManageTransactionModalConfig()

    @Serializable
    data object CreateAccount : ManageTransactionModalConfig()
}

@JsExport
sealed class ManageTransactionModalChild {

    @Suppress("unused")
    class CreateCategoryChild(val component: CreateCategoryComponent) : ManageTransactionModalChild()

    @Suppress("unused")
    class CreateAccountChild(val component: CreateAccountComponent) : ManageTransactionModalChild()
}