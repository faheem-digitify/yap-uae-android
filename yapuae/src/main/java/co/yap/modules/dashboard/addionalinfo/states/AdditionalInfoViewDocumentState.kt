package co.yap.modules.dashboard.addionalinfo.states

import android.app.Application
import androidx.databinding.ObservableField
import co.yap.modules.dashboard.addionalinfo.interfaces.IAdditionalInfoViewDocument
import co.yap.yapcore.BaseState

class AdditionalInfoViewDocumentState(application: Application) : BaseState(),
    IAdditionalInfoViewDocument.State {
    override val documentName: ObservableField<String> = ObservableField("")

}