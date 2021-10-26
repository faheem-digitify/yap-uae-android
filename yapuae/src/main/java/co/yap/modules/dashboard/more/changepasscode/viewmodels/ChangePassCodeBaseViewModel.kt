package co.yap.modules.dashboard.more.changepasscode.viewmodels

import android.app.Application
import co.yap.yapcore.BaseViewModel
import co.yap.yapcore.IBase

abstract class ChangePassCodeBaseViewModel<S : IBase.State>(application: Application) :
    BaseViewModel<S>(application) {
    var parentViewModel: ChangePassCodeViewModel? = null

    fun setToolBarTitle(title: String) {
        parentViewModel?.state?.toolbarTitle = title
    }

    fun toggleToolBarVisibility(visibility: Boolean) {
        parentViewModel?.state?.toolbarVisibility = visibility
    }
}