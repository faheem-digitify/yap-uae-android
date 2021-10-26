package co.yap.widgets.bottomsheet

import android.view.View
import co.yap.networking.coreitems.CoreBottomSheetData
import co.yap.yapcore.interfaces.OnItemClickListener

class CoreBottomSheetItemViewModel(
    private val bottomSheetItem: CoreBottomSheetData,
    val position: Int,
    private val onItemClickListener: OnItemClickListener?
) {
    var itemBottomSheet : CoreBottomSheetData = bottomSheetItem
    fun onViewClicked(view: View) {
        onItemClickListener?.onItemClick(view, bottomSheetItem, position)
    }
}