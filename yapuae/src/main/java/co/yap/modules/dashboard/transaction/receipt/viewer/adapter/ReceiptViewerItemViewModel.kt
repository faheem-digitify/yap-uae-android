package co.yap.modules.dashboard.transaction.receipt.viewer.adapter

import co.yap.networking.transactions.responsedtos.ReceiptModel

class ReceiptViewerItemViewModel(
    val receipt: ReceiptModel,
    val position: Int
)