package co.yap.modules.dashboard.transaction.category

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import co.yap.yapuae.R
import co.yap.yapuae.databinding.ItemTapixCategoryBinding
import co.yap.networking.transactions.responsedtos.transaction.TapixCategory
import co.yap.yapcore.BaseBindingRecyclerAdapter

class TransactionCategoryAdapter(
    val list: MutableList<TapixCategory>
) :
    BaseBindingRecyclerAdapter<TapixCategory, RecyclerView.ViewHolder>(list) {
    override fun onCreateViewHolder(binding: ViewDataBinding): RecyclerView.ViewHolder {
        return TransactionCategoryViewHolder(
            binding as ItemTapixCategoryBinding
        )
    }

    override fun getLayoutIdForViewType(viewType: Int): Int = R.layout.item_tapix_category

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        if (holder is TransactionCategoryViewHolder) {

            holder.onBind(position, list[position], onItemClickListener)
        }
    }
}