package co.yap.modules.dashboard.addionalinfo.viewmodels

import android.app.Application
import co.yap.modules.dashboard.addionalinfo.interfaces.IAdditionalInfoScanDocument
import co.yap.modules.dashboard.addionalinfo.states.AdditionalInfoScanDocumentState

class AdditionalInfoScanDocumentViewModel(application: Application) :
    AdditionalInfoBaseViewModel<IAdditionalInfoScanDocument.State>(application = application),
    IAdditionalInfoScanDocument.ViewModel {
    override val state: IAdditionalInfoScanDocument.State = AdditionalInfoScanDocumentState()
}