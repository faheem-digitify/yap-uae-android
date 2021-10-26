package co.yap.modules.dashboard.addionalinfo.states

import androidx.databinding.ObservableField
import co.yap.modules.dashboard.addionalinfo.interfaces.IAdditionalInfoScanDocument
import co.yap.yapcore.BaseState

class AdditionalInfoScanDocumentState : BaseState(), IAdditionalInfoScanDocument.State {
    override val documentName: ObservableField<String> = ObservableField("")
}