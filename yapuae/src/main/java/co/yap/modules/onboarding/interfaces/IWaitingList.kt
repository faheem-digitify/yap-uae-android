package co.yap.modules.onboarding.interfaces

import androidx.databinding.ObservableField
import co.yap.networking.customers.responsedtos.WaitingRankingResponse
import co.yap.yapcore.IBase
import co.yap.yapcore.SingleClickEvent

interface IWaitingList {

    interface View : IBase.View<ViewModel> {
        fun showGainPointsNotification()
        fun setObservers()
        fun removeObservers()
    }

    interface ViewModel : IBase.ViewModel<State> {
        val clickEvent: SingleClickEvent
        fun onPressView(id: Int)
        fun requestWaitingRanking(success: (showNotification: Boolean) -> Unit)
        fun stopRankingMsgRequest()
        var inviteeDetails: ArrayList<WaitingRankingResponse.InviteeDetails>?
    }

    interface State : IBase.State {
        var waitingBehind: ObservableField<String>?
        var rank: ObservableField<String>?
        var jump: ObservableField<String>?
        var gainPoints: ObservableField<String>?
        var rankList: MutableList<Int>?
        var signedUpUsers: ObservableField<String>?
        var totalGainedPoints: ObservableField<String>?
    }
}