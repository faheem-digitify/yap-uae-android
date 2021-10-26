package co.yap.modules.dashboard.addionalinfo.interfaces

import androidx.databinding.ObservableField
import co.yap.yapcore.IBase

interface IAdditionalInfoScanDocument {
    interface View : IBase.View<ViewModel>

    interface ViewModel : IBase.ViewModel<State>

    interface State : IBase.State {
        val documentName: ObservableField<String>
    }
}