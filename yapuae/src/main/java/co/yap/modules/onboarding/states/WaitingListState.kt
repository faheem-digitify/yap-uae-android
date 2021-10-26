package co.yap.modules.onboarding.states

import androidx.databinding.ObservableField
import co.yap.modules.onboarding.interfaces.IWaitingList
import co.yap.yapcore.BaseState

class WaitingListState : BaseState(), IWaitingList.State {
    override var waitingBehind: ObservableField<String>? = ObservableField("0")
    override var jump: ObservableField<String>? = ObservableField("0")
    override var rank: ObservableField<String>? = ObservableField("0")
    override var gainPoints: ObservableField<String>? = ObservableField("0")
    override var rankList: MutableList<Int>? = mutableListOf()
    override var signedUpUsers: ObservableField<String>? = ObservableField("0")
    override var totalGainedPoints: ObservableField<String>? = ObservableField("0")
}