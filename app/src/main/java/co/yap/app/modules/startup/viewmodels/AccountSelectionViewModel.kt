package co.yap.app.modules.startup.viewmodels

import android.app.Application
import co.yap.app.modules.startup.interfaces.IAccountSelection
import co.yap.app.modules.startup.states.AccountSelectionState
import co.yap.yapcore.BaseViewModel
import co.yap.yapcore.SingleClickEvent

class AccountSelectionViewModel(application: Application) :
    BaseViewModel<IAccountSelection.State>(application),
    IAccountSelection.ViewModel {

    override val state: AccountSelectionState = AccountSelectionState()
    override val clickEvent: SingleClickEvent = SingleClickEvent()

    override fun handlePressOnView(id: Int) {
        clickEvent.setValue(id)
    }
}