package co.yap.widgets.bottomsheet.bottomsheet_with_initials

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import co.yap.networking.coreitems.CoreBottomSheetData
import co.yap.widgets.bottomsheet.CoreBottomSheetAdapter
import co.yap.widgets.bottomsheet.CoreBottomSheetItemViewModel
import co.yap.yapcore.R
import co.yap.yapcore.constants.Constants
import co.yap.yapcore.databinding.ItemInitialsBottomSheetBinding
import co.yap.yapcore.interfaces.OnItemClickListener

class CoreInitialsBottomSheetAdapter(
    private val list: MutableList<CoreBottomSheetData>,
    private val viewType: Int = Constants.VIEW_WITHOUT_FLAG
) : CoreBottomSheetAdapter(list, viewType) {
    override fun getLayoutIdForViewType(viewType: Int): Int =
        R.layout.item_initials_bottom_sheet

    override fun onCreateViewHolder(binding: ViewDataBinding): RecyclerView.ViewHolder {
        return BottomSheetInitialViewHolder(binding as ItemInitialsBottomSheetBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as BottomSheetInitialViewHolder).onBind(
            list[position],
            position,
            onItemClickListener
        )
    }

    override fun getItemViewType(position: Int): Int {
        return viewType
    }
}

class BottomSheetInitialViewHolder(private val itemInitialsBottomSheetBinding: ItemInitialsBottomSheetBinding) :
    RecyclerView.ViewHolder(itemInitialsBottomSheetBinding.root) {
    fun onBind(
        bottomSheetItem: CoreBottomSheetData,
        position: Int,
        onItemClickListener: OnItemClickListener?
    ) {
        itemInitialsBottomSheetBinding.viewModel =
            CoreBottomSheetItemViewModel(
                bottomSheetItem = bottomSheetItem,
                position = position,
                onItemClickListener = onItemClickListener
            )
        itemInitialsBottomSheetBinding.executePendingBindings()
    }
}
