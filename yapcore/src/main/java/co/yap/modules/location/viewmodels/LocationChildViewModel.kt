package co.yap.modules.location.viewmodels

import android.app.Application
import co.yap.modules.location.interfaces.ILocation
import co.yap.yapcore.BaseViewModel
import co.yap.yapcore.IBase

abstract class LocationChildViewModel<S : IBase.State>(application: Application) :
    BaseViewModel<S>(application) {
    var parentViewModel: ILocation.ViewModel? = null

    fun setProgress(percent: Int) {
        parentViewModel?.state?.currentProgress = percent
    }

    fun progressToolBarVisibility(visibility: Boolean) {
        parentViewModel?.state?.toolbarVisibility?.set(visibility)
    }
}
