package co.yap.modules.dashboard.transaction.receipt.viewer.adapter

import androidx.databinding.ViewDataBinding
import co.yap.yapuae.databinding.ItemTransactionReceiptViewBinding
import co.yap.yapcore.BaseBindingRecyclerAdapter
import co.yap.yapuae.R
import co.yap.networking.transactions.responsedtos.ReceiptModel

class ReceiptViewerAdapter(private val list: MutableList<ReceiptModel>) :
    BaseBindingRecyclerAdapter<ReceiptModel, ReceiptViewerViewHolder>(list) {

    override fun onCreateViewHolder(binding: ViewDataBinding): ReceiptViewerViewHolder {
        return ReceiptViewerViewHolder(
            binding as ItemTransactionReceiptViewBinding
        )
    }

    override fun onBindViewHolder(holder: ReceiptViewerViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.onBind(position, list[position])
    }

    override fun getLayoutIdForViewType(viewType: Int): Int = R.layout.item_transaction_receipt_view
}
