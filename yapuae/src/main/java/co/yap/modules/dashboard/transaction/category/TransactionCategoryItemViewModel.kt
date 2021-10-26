package co.yap.modules.dashboard.transaction.category

import android.view.View
import co.yap.networking.transactions.responsedtos.transaction.TapixCategory
import co.yap.yapcore.interfaces.OnItemClickListener

class TransactionCategoryItemViewModel(
    val item: TapixCategory,
    val position: Int, private val onItemClickListener: OnItemClickListener?
) {
    fun handlePressOnView(view: View) {
        onItemClickListener?.onItemClick(view, item, position)
    }
}