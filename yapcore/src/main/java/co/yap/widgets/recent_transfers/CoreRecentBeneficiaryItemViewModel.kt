package co.yap.widgets.recent_transfers

import android.view.View
import co.yap.networking.customers.responsedtos.sendmoney.CoreRecentBeneficiaryItem
import co.yap.yapcore.interfaces.OnItemClickListener

class CoreRecentBeneficiaryItemViewModel(
    var coreRecentBeneficiary: CoreRecentBeneficiaryItem,
    var position: Int,
    private val onItemClickListener: OnItemClickListener?
) {
    fun handlePressOnView(view: View) {
        onItemClickListener?.onItemClick(view, coreRecentBeneficiary, position)
    }
}
