package co.yap.modules.reachonthetop

import android.content.Intent
import co.yap.yapcore.IBase
import co.yap.yapcore.SingleClickEvent

interface IReachedQueueTop {

    interface View : IBase.View<ViewModel> {
        fun setObservers()
        fun removeObservers()
        fun setCardAnimation()
        fun handleKYCRequestResult(data: Intent?)
        fun handleLocationRequestResult(data: Intent?)
        fun goToDashboard()
    }

    interface ViewModel : IBase.ViewModel<State> {
        val clickEvent: SingleClickEvent
        fun handlePressOnNext(id: Int)
        fun completeVerification(callBack: () -> Unit)
    }

    interface State : IBase.State {
        var firstName: String
        var countryCode: String
        var mobileNo: String
    }
}