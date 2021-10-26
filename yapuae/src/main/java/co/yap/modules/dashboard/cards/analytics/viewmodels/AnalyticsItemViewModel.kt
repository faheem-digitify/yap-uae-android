package co.yap.modules.dashboard.cards.analytics.viewmodels

import android.view.View
import androidx.core.os.bundleOf
import co.yap.networking.transactions.responsedtos.TxnAnalytic
import co.yap.yapcore.firebase.FirebaseEvent
import co.yap.yapcore.firebase.trackEventWithScreenName
import co.yap.yapcore.interfaces.OnItemClickListener

class AnalyticsItemViewModel(
    val analyticsItem: TxnAnalytic?,
    val position: Int,
    private val onItemClickListener: OnItemClickListener?
) {

    fun onViewClicked(view: View) {
        trackEventWithScreenName(
            FirebaseEvent.CLICK_CATEGORY_LIST,
            bundleOf("spend_category" to (analyticsItem?.title ?: ""))
        )
        onItemClickListener?.onItemClick(view, analyticsItem!!, position)
    }

}
