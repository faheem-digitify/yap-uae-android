package co.yap.modules.dashboard.home.adaptor

import co.yap.networking.transactions.responsedtos.transaction.Transaction

class ItemAnalyticsTransactionVM(
    val item: Transaction,
    val position: Int,
    val analyticsItemTitle: String? = null,
    val analyticsItemImgUrl: String? = null
)