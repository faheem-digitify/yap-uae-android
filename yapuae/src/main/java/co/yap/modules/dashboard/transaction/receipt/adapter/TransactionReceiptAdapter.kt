package co.yap.modules.dashboard.transaction.receipt.adapter

import androidx.databinding.ViewDataBinding
import co.yap.yapuae.R
import co.yap.yapuae.databinding.ItemTransectionReciptBinding
import co.yap.modules.dashboard.transaction.receipt.adapter.ReceiptViewHolder
import co.yap.networking.transactions.responsedtos.ReceiptModel
import co.yap.yapcore.BaseBindingRecyclerAdapter

class TransactionReceiptAdapter(
    val listItems: MutableList<ReceiptModel>
) :
    BaseBindingRecyclerAdapter<ReceiptModel, ReceiptViewHolder>(listItems) {
    override fun onCreateViewHolder(binding: ViewDataBinding): ReceiptViewHolder {
        return ReceiptViewHolder(
            binding as ItemTransectionReciptBinding
        )
    }

    override fun getLayoutIdForViewType(viewType: Int): Int = R.layout.item_transection_recipt
    override fun onBindViewHolder(holder: ReceiptViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.onBind(listItems[position], position, onItemClickListener)
    }
}