package co.yap.widgets.bottomsheet

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import co.yap.yapcore.BaseBindingRecyclerAdapter
import co.yap.yapcore.R
import co.yap.yapcore.databinding.CoreItemBottomSheetBinding
import co.yap.yapcore.interfaces.OnItemClickListener

class BottomSheetAdapter(private val list: MutableList<BottomSheetItem>) :
    BaseBindingRecyclerAdapter<BottomSheetItem, RecyclerView.ViewHolder>(list) {

    override fun getLayoutIdForViewType(viewType: Int): Int = R.layout.core_item_bottom_sheet

    override fun onCreateViewHolder(binding: ViewDataBinding): RecyclerView.ViewHolder {
        return BottomSheetItemViewHolder(binding as CoreItemBottomSheetBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is BottomSheetItemViewHolder -> {
                holder.onBind(list[position], position, onItemClickListener)
            }
        }
    }
}

class BottomSheetItemViewHolder(private val coreItemBottomSheetBinding: CoreItemBottomSheetBinding) :
    RecyclerView.ViewHolder(coreItemBottomSheetBinding.root) {

    fun onBind(
        bottomSheetItem: BottomSheetItem,
        position: Int,
        onItemClickListener: OnItemClickListener?
    ) {
        coreItemBottomSheetBinding.viewModel =
            BottomSheetItemViewModel(bottomSheetItem, position, onItemClickListener)
        coreItemBottomSheetBinding.executePendingBindings()
    }
}

class BottomSheetItemViewModel(
    val bottomSheetItem: BottomSheetItem,
    private val position: Int,
    private val onItemClickListener: OnItemClickListener?
) {
    fun onViewClicked(view: View) {
        onItemClickListener?.onItemClick(view, bottomSheetItem, position)
    }
}