package co.yap.modules.dashboard.transaction.receipt.add

import co.yap.yapcore.IBase
import co.yap.yapcore.SingleClickEvent

class IAddTransactionReceipt {
    interface View : IBase.View<ViewModel> {
        fun onCaptureProcessCompleted(filename: String?)
        fun registerObserver()
        fun unRegisterObserver()
    }

    interface ViewModel : IBase.ViewModel<State> {
        val clickEvent: SingleClickEvent
        fun handlePressOnView(id: Int)
    }

    interface State : IBase.State {
    }
}
