package co.yap.sendmoney.home.main

import android.app.Application
import co.yap.yapcore.BaseViewModel
import co.yap.yapcore.IBase

abstract class SMBeneficiaryParentBaseViewModel<S : IBase.State>(application: Application) :
    BaseViewModel<S>(application) {
    var parentViewModel: ISMBeneficiaryParent.ViewModel? = null

    fun setToolBarTitle(title: String) {
        parentViewModel?.state?.toolbarTitle = title
    }
}
