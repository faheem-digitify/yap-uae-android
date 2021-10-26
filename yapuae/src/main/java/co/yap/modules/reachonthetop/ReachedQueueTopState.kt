package co.yap.modules.reachonthetop

import co.yap.yapcore.BaseState

class ReachedQueueTopState : BaseState(), IReachedQueueTop.State {
    override var firstName: String = ""
    override var countryCode: String = ""
    override var mobileNo: String = ""
}
