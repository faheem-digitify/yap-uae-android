package co.yap.widgets.bottomsheet

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import co.yap.networking.coreitems.CoreBottomSheetData
import co.yap.yapcore.BaseBindingSearchRecylerAdapter
import co.yap.yapcore.R
import co.yap.yapcore.constants.Constants
import co.yap.yapcore.databinding.ItemBottomSheetNoSeparatorBinding
import co.yap.yapcore.databinding.ItemBottomsheetWithFlagBinding
import co.yap.yapcore.databinding.ItemCityBinding
import co.yap.yapcore.interfaces.OnItemClickListener

open class CoreBottomSheetAdapter(
    private val list: MutableList<CoreBottomSheetData>,
    private val viewType: Int = Constants.VIEW_WITHOUT_FLAG
) : BaseBindingSearchRecylerAdapter<CoreBottomSheetData, RecyclerView.ViewHolder>(list) {

    override fun getLayoutIdForViewType(viewType: Int): Int = when (viewType) {
        Constants.VIEW_ITEM_WITHOUT_SEPARATOR -> R.layout.item_bottom_sheet_no_separator
        Constants.VIEW_WITH_FLAG -> R.layout.item_bottomsheet_with_flag
        else -> R.layout.item_city
    }

    override fun onCreateViewHolder(binding: ViewDataBinding): RecyclerView.ViewHolder {
        return when (binding) {
            is ItemBottomsheetWithFlagBinding -> {
                BottomSheetWithFlagViewHolder(binding)
            }
            is ItemCityBinding -> {
                BottomSheetViewHolder(binding)
            }
            is ItemBottomSheetNoSeparatorBinding -> {
                BottomSheetWithNoSeparatorViewHolder(binding)
            }
            else -> {
                BottomSheetViewHolder(binding as ItemCityBinding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is BottomSheetViewHolder -> {
                holder.onBind(list[position], position, onItemClickListener)
            }
            is BottomSheetWithFlagViewHolder -> {
                holder.onBind(list[position], position, onItemClickListener)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return viewType
    }

    override fun filterItem(constraint: CharSequence?, item: CoreBottomSheetData): Boolean {
        val filterString = constraint.toString().toLowerCase()
        val name = item.subTitle?.toLowerCase() ?: ""
        return name.contains(filterString)
    }
}

class BottomSheetViewHolder(private val itemCityBinding: ItemCityBinding) :
    RecyclerView.ViewHolder(itemCityBinding.root) {
    fun onBind(
        bottomSheetItem: CoreBottomSheetData,
        position: Int,
        onItemClickListener: OnItemClickListener?
    ) {
        itemCityBinding.viewModel =
            CoreBottomSheetItemViewModel(
                bottomSheetItem = bottomSheetItem,
                position = position,
                onItemClickListener = onItemClickListener
            )
        itemCityBinding.executePendingBindings()
    }

}

class BottomSheetWithFlagViewHolder(private val itemFlagBinding: ItemBottomsheetWithFlagBinding) :
    RecyclerView.ViewHolder(itemFlagBinding.root) {
    fun onBind(
        bottomSheetItem: CoreBottomSheetData,
        position: Int,
        onItemClickListener: OnItemClickListener?
    ) {
        itemFlagBinding.viewModel = CoreBottomSheetItemViewModel(
            bottomSheetItem = bottomSheetItem,
            position = position,
            onItemClickListener = onItemClickListener
        )
        itemFlagBinding.executePendingBindings()
    }
}

class BottomSheetWithNoSeparatorViewHolder(private val itemBinding: ItemBottomSheetNoSeparatorBinding) :
    RecyclerView.ViewHolder(itemBinding.root) {
    fun onBind(
        bottomSheetItem: CoreBottomSheetData,
        position: Int,
        onItemClickListener: OnItemClickListener?
    ) {
        itemBinding.viewModel = CoreBottomSheetItemViewModel(
            bottomSheetItem = bottomSheetItem,
            position = position,
            onItemClickListener = onItemClickListener
        )
        itemBinding.executePendingBindings()
    }
}
