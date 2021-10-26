package co.yap.modules.dashboard.addionalinfo.viewmodels

import android.app.Application
import co.yap.modules.dashboard.addionalinfo.interfaces.IAdditionalInfoViewDocument
import co.yap.modules.dashboard.addionalinfo.states.AdditionalInfoViewDocumentState
import co.yap.yapcore.SingleClickEvent

class AdditionalInfoViewDocumentViewModel(application: Application) :
    AdditionalInfoBaseViewModel<IAdditionalInfoViewDocument.State>(application = application),
    IAdditionalInfoViewDocument.ViewModel {
    override val state: IAdditionalInfoViewDocument.State =
        AdditionalInfoViewDocumentState(application)
    override val clickEvent: SingleClickEvent = SingleClickEvent()

    override fun handleOnPressView(id: Int) {
        clickEvent.setValue(id)
    }
}