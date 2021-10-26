package co.yap.modules.dashboard.transaction.receipt.viewer.adapter

import androidx.recyclerview.widget.RecyclerView
import co.yap.yapuae.databinding.ItemTransactionReceiptViewBinding
import co.yap.networking.transactions.responsedtos.ReceiptModel

class ReceiptViewerViewHolder(private val itemTransactionReceiptViewBinding: ItemTransactionReceiptViewBinding) :
    RecyclerView.ViewHolder(itemTransactionReceiptViewBinding.root) {

    fun onBind(
        position: Int,
        receipt: ReceiptModel
    ) {
        itemTransactionReceiptViewBinding.viewModel =
            ReceiptViewerItemViewModel(
                receipt,
                position
            )

        itemTransactionReceiptViewBinding.executePendingBindings()
    }
}
