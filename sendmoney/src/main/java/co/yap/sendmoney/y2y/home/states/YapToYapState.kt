package co.yap.sendmoney.y2y.home.states

import androidx.databinding.ObservableBoolean
import co.yap.sendmoney.y2y.home.interfaces.IYapToYap
import co.yap.yapcore.BaseState

class YapToYapState : BaseState(), IYapToYap.State {
    override var isRecentsVisible: ObservableBoolean = ObservableBoolean(false)
    override var isNoRecents: ObservableBoolean = ObservableBoolean(true)
}