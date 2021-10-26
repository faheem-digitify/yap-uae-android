package co.yap.modules.dashboard.transaction.feedback

import android.app.Application
import androidx.databinding.ObservableBoolean
import co.yap.modules.dashboard.transaction.feedback.adaptor.TransactionFeedbackAdapter
import co.yap.networking.transactions.FeedbackTransactions.ItemFeedback
import co.yap.yapcore.BaseViewModel
import co.yap.yapcore.SingleClickEvent

class TransactionFeedbackViewModel(application: Application) :
    BaseViewModel<ITransactionFeedback.State>(application), ITransactionFeedback.ViewModel {

    override var clickEvent: SingleClickEvent = SingleClickEvent()

    override var feedbackSelected: ObservableBoolean = ObservableBoolean(false)
    override val adapter: TransactionFeedbackAdapter = TransactionFeedbackAdapter(mutableListOf())

    override val state: TransactionFeedbackState = TransactionFeedbackState()

    override fun onCreate() {
        super.onCreate()
        adapter.setList(getImprovementComponent())
    }

    override fun handlePressOnView(id: Int) {
        clickEvent.setValue(id)
    }

    private fun getImprovementComponent(): MutableList<ItemFeedback> = mutableListOf<ItemFeedback>(
        ItemFeedback(label = "Logo"), ItemFeedback(label = "Location"),
        ItemFeedback(label = "Name of merchant"),
        ItemFeedback(label = "Category")
    )

    override fun selectFeedback(pos: Int) {
        adapter.getDataList()[pos].isCheck =
            !adapter.getDataList()[pos].isCheck
        adapter.notifyItemChanged(pos)
        val list = adapter.getDataList().filter {
            it.isCheck
        }
        feedbackSelected.set(!list.isNullOrEmpty())
    }
}
