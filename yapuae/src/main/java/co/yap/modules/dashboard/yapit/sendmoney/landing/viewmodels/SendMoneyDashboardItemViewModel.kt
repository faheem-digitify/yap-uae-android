package co.yap.modules.dashboard.yapit.sendmoney.landing.viewmodels

import android.view.View
import co.yap.modules.dashboard.yapit.sendmoney.main.SendMoneyOptions
import co.yap.yapcore.interfaces.OnItemClickListener

class SendMoneyDashboardItemViewModel(
    var sendMoneyOptions: SendMoneyOptions,
    var position: Int,
    private val onItemClickListener: OnItemClickListener?
) {
    fun handlePressOnView(view: View) {
        onItemClickListener?.onItemClick(view, sendMoneyOptions, position)
    }
}
