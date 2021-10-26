package co.yap.sendmoney.y2y.home.phonecontacts

import android.view.View
import co.yap.networking.customers.responsedtos.sendmoney.IBeneficiary
import co.yap.yapcore.interfaces.OnItemClickListener

class YapContactItemViewModel(
    val contact: IBeneficiary?,
    val position: Int,
    private val onItemClickListener: OnItemClickListener?
) {

    fun onViewClicked(view: View) {
        contact?.let { onItemClickListener?.onItemClick(view, it, position) }

    }

}