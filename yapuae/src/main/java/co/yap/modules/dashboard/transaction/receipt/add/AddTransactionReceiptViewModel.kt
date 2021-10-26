package co.yap.modules.dashboard.transaction.receipt.add

import android.app.Application
import co.yap.yapcore.BaseViewModel
import co.yap.yapcore.SingleClickEvent

class AddTransactionReceiptViewModel(application: Application) :
    BaseViewModel<IAddTransactionReceipt.State>(application), IAddTransactionReceipt.ViewModel {
    override val state = AddTransactionReceiptState()
    override val clickEvent = SingleClickEvent()

    override fun handlePressOnView(id: Int) {
        clickEvent.postValue(id)
    }
}
