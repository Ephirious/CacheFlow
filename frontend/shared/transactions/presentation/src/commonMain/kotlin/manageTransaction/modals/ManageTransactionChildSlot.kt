package manageTransaction.modals

import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.value.Value
import editors.accounts.RealCreateAccountComponent
import editors.accounts.mvi.CreateAccountContainer
import editors.categories.RealCreateCategoryComponent
import editors.categories.mvi.CreateCategoryContainer
import manageTransaction.RealManageTransactionComponent
import org.koin.core.component.get

fun RealManageTransactionComponent.modalChild(): Value<ChildSlot<ManageTransactionModalConfig, ManageTransactionModalChild>> =
    childSlot(
        source = modalNavigation,
        serializer = null, // т.к. после перезагрузки багуется в вебе (?) //ManageTransactionConfig.serializer(),
        handleBackButton = false,
    ) { config, childCtx ->
        when (config) {

            ManageTransactionModalConfig.CreateAccount -> ManageTransactionModalChild.CreateAccountChild(
                RealCreateAccountComponent(
                    childCtx, container = {
                        CreateAccountContainer(
                            createAccountUseCase = get(),
                            closeModal = ::dismissSlot
                        )
                    }
                )
            )

            ManageTransactionModalConfig.CreateCategory -> ManageTransactionModalChild.CreateCategoryChild(
                RealCreateCategoryComponent(
                    childCtx, container = {
                        CreateCategoryContainer(
                            createCategoryUseCase = get(),
                            closeModal = ::dismissSlot
                        )
                    }
                )
            )
        }
    }