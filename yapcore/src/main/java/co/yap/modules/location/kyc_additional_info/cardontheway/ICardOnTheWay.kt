package co.yap.modules.location.kyc_additional_info.cardontheway

import androidx.databinding.ObservableField
import co.yap.networking.coreitems.CoreBottomSheetData
import co.yap.yapcore.IBase
import co.yap.yapcore.SingleClickEvent

interface ICardOnTheWay {
    interface State : IBase.State {
        var bottomSheetItems: ObservableField<MutableList<CoreBottomSheetData>>
    }

    interface ViewModel : IBase.ViewModel<State> {
        val clickEvent: SingleClickEvent
        fun handlePressOnView(id: Int)
    }

    interface View : IBase.View<ViewModel> {
        fun setCardAnimation()
        fun setObservers()
        fun removeObservers()
    }
}