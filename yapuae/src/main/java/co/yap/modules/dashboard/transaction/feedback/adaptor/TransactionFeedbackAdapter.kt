package co.yap.modules.dashboard.transaction.feedback.adaptor

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import co.yap.yapuae.R
import co.yap.yapuae.databinding.ItemImprovementComponentsBinding
import co.yap.networking.transactions.FeedbackTransactions.ItemFeedback
import co.yap.yapcore.BaseBindingRecyclerAdapter

class TransactionFeedbackAdapter(val list: MutableList<ItemFeedback>) :
    BaseBindingRecyclerAdapter<ItemFeedback, RecyclerView.ViewHolder>(list) {

    override fun onCreateViewHolder(binding: ViewDataBinding): RecyclerView.ViewHolder {
        return TransactionFeedbackViewHolder(binding as ItemImprovementComponentsBinding)
    }

    override fun getLayoutIdForViewType(viewType: Int): Int = R.layout.item_improvement_components

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        if (holder is TransactionFeedbackViewHolder) {
            holder.onBind(position, list[position], onItemClickListener)
        }
    }
}