package co.yap.modules.dashboard.transaction.feedback.adaptor

import android.view.View
import co.yap.networking.transactions.FeedbackTransactions.ItemFeedback
import co.yap.yapcore.interfaces.OnItemClickListener

class TransactionFeedbackItemViewModel(
    val item: ItemFeedback,
    val position: Int, private val onItemClickListener: OnItemClickListener?
) {
    fun onViewClicked(view: View) {
        onItemClickListener?.onItemClick(view, item, position)
    }
}