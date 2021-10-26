package co.yap.modules.dashboard.transaction.feedback.adaptor

import androidx.recyclerview.widget.RecyclerView
import co.yap.yapuae.databinding.ItemImprovementComponentsBinding
import co.yap.networking.transactions.FeedbackTransactions.ItemFeedback
import co.yap.yapcore.interfaces.OnItemClickListener

class TransactionFeedbackViewHolder(val binding: ItemImprovementComponentsBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun onBind(
        positiion: Int,
        item: ItemFeedback,
        onItemClickListener: OnItemClickListener?
    ) {
        binding.viewmodel = TransactionFeedbackItemViewModel(item, positiion, onItemClickListener)
        binding.executePendingBindings()
    }
}