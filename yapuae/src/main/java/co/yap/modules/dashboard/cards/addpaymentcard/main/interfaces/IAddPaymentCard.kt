package co.yap.modules.dashboard.cards.addpaymentcard.main.interfaces

import co.yap.networking.cards.responsedtos.VirtualCardDesigns
import co.yap.yapcore.IBase
import co.yap.yapcore.SingleLiveEvent

interface IAddPaymentCard {

    interface State : IBase.State {
        var tootlBarTitle: String
        var tootlBarVisibility: Int
    }

    interface ViewModel : IBase.ViewModel<State> {
        fun handlePressOnBackButton()
        fun getVirtualCardDesigns(success: () -> Unit)
        val backButtonPressEvent: SingleLiveEvent<Boolean>
        var physicalCardFee: String
        var virtualCardFee: String
        var virtualCardDesignsList: ArrayList<VirtualCardDesigns>
        var selectedVirtualCard: VirtualCardDesigns?
    }

    interface View : IBase.View<ViewModel>
}