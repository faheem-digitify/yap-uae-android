package co.yap.modules.dashboard.transaction.receipt.adapter

import android.view.View
import co.yap.networking.transactions.responsedtos.ReceiptModel
import co.yap.yapcore.interfaces.OnItemClickListener

class TransactionReceiptItemViewModel(
    private val itemReceipt: ReceiptModel,
    val position: Int,
    private val onItemClickListener: OnItemClickListener?
) {
    fun handlePressOnView(view: View) {
        onItemClickListener?.onItemClick(view, itemReceipt, position)
    }
}