package co.yap.modules.dashboard.addionalinfo.viewmodels

import android.app.Application
import co.yap.modules.dashboard.addionalinfo.interfaces.IAdditionalInfo
import co.yap.networking.customers.models.additionalinfo.AdditionalDocument
import co.yap.networking.customers.models.additionalinfo.AdditionalQuestion
import co.yap.yapcore.BaseViewModel
import co.yap.yapcore.IBase

abstract class AdditionalInfoBaseViewModel<S : IBase.State>(application: Application) :
    BaseViewModel<S>(application) {
    var parentViewModel: IAdditionalInfo.ViewModel? = null

    fun moveStep() {
        parentViewModel?.stepCount?.value = parentViewModel?.stepCount?.value?.plus(1)
    }

    fun showHeader(isShow: Boolean) {
        parentViewModel?.state?.showHeader?.set(isShow)
    }

    fun getDocumentList(): ArrayList<AdditionalDocument> {
        return parentViewModel?.state?.documentList ?: arrayListOf()
    }

    fun getQuestionList(): ArrayList<AdditionalQuestion> {
        return parentViewModel?.state?.questionList ?: arrayListOf()
    }

    fun getScreenType(): String {
        return parentViewModel?.state?.screenType?.get() ?: ""
    }

    fun submitInfo(success: () -> Unit) {
        parentViewModel?.submitAdditionalInfo {
            success.invoke()
        }
    }

}