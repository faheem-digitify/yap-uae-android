package co.yap.modules.dashboard.more.changepasscode.viewmodels

import android.app.Application
import co.yap.modules.dashboard.more.changepasscode.interfaces.IChangePassCodeSuccess
import co.yap.modules.dashboard.more.changepasscode.states.ChangePassCodeSuccessState
import co.yap.yapcore.SingleClickEvent

class ChangePasscodeSuccessViewModel(application: Application) :
    ChangePassCodeBaseViewModel<IChangePassCodeSuccess.State>(application),
    IChangePassCodeSuccess.ViewModel {
    override val buttonClickEvent: SingleClickEvent = SingleClickEvent()
    override val state: ChangePassCodeSuccessState = ChangePassCodeSuccessState()
    override fun onCreate() {
        super.onCreate()
        parentViewModel?.state?.toolbarVisibility = false
    }

    override fun handlePressOnDoneButton() {
        buttonClickEvent.call()
    }
}