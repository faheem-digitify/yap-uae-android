package co.yap.modules.dashboard.transaction.detail.models

import co.yap.networking.transactions.responsedtos.transaction.TapixCategory

data class TransactionDetail(
    val transactionTitle: String?,
    val noteValue: String?,
    val noteAddedDate: String?,
    val categoryTitle: String?,
    val totalAmount: Double?,
    val locationValue: String?,
    val transferType: String?,
    val categoryIcon: Int?,
    val statusIcon: Int?,
    val coverImage: Int,
    val transactionItem: List<ItemTransactionDetail>,
    val showTotalPurchase: Boolean?,
    val showError: Boolean?,
    val showReceipts: Boolean?,
    val isAtmTransaction: Boolean?,
    val showCategory: Boolean?,
    val categoryType: Boolean?,
    val categoryDescription: String?,
    val tapixCategory: TapixCategory?,
    val showFeedBack: Boolean?,
    val isYTYTransfer: Boolean? = false,
    val isDeclinedTransaction: Boolean? = false,
    val isMApVisible: Boolean? = false
)


