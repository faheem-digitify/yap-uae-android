package co.yap.modules.dashboard.transaction.category

import androidx.recyclerview.widget.RecyclerView
import co.yap.yapuae.R
import co.yap.yapuae.databinding.ItemTapixCategoryBinding
import co.yap.networking.transactions.responsedtos.transaction.TapixCategory
import co.yap.yapcore.interfaces.OnItemClickListener

class TransactionCategoryViewHolder(val binding: ItemTapixCategoryBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun onBind(
        position: Int,
        item: TapixCategory,
        onItemClickListener: OnItemClickListener?
    ) {
        binding.viewModel =
            TransactionCategoryItemViewModel(
                item,
                position, onItemClickListener
            )

        binding.clCategory.foreground = binding.clCategory.context.getDrawable(R.drawable.bg_grey_item_selected_ripple)
        binding.executePendingBindings()
    }
}