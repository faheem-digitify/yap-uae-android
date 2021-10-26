package co.yap.modules.dashboard.cards.addpaymentcard.spare.main.interfaces

import androidx.databinding.ObservableField
import co.yap.modules.dashboard.cards.addpaymentcard.spare.helpers.virtual.AddSpareVirtualCardLogicHelper
import co.yap.networking.cards.responsedtos.Card
import co.yap.yapcore.IBase
import co.yap.yapcore.SingleClickEvent

interface IAddSpareCard {
    interface State : IBase.State {
        var cardType: String
        var cardName: String
        var virtualCardFee: String
        var coreButtonText: String
        var availableBalance: ObservableField<CharSequence>
    }

    interface ViewModel : IBase.ViewModel<State> {
        val clickEvent: SingleClickEvent
        var cardType: String
        val addSpareVirtualCardLogicHelper: AddSpareVirtualCardLogicHelper
        var paymentCard: Card?
        var cardName: String?

        val CONFIRM_VIRTUAL_PURCHASE: Int
        val ADD_VIRTUAL_SPARE_SUCCESS_EVENT: Int

        fun handlePressOnView(id: Int)
        fun requestAddSpareVirtualCard()
        fun isEnoughBalance(): Boolean
    }

    interface View : IBase.View<ViewModel>
}