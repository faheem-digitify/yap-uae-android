package co.yap.modules.dashboard.addionalinfo.interfaces

import androidx.databinding.ObservableField
import co.yap.yapcore.IBase
import co.yap.yapcore.SingleClickEvent

interface IAdditionalInfoViewDocument {
    interface View : IBase.View<ViewModel>

    interface ViewModel : IBase.ViewModel<State> {
        val clickEvent: SingleClickEvent
        fun handleOnPressView(id: Int)
    }

    interface State : IBase.State {
        val documentName: ObservableField<String>
    }
}