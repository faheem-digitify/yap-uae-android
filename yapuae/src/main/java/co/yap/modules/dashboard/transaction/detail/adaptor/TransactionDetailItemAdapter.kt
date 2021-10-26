package co.yap.modules.dashboard.transaction.detail.adaptor

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import co.yap.yapuae.R
import co.yap.yapuae.databinding.ItemTransactionDetailBinding
import co.yap.modules.dashboard.transaction.detail.models.ItemTransactionDetail
import co.yap.yapcore.BaseBindingRecyclerAdapter

class TransactionDetailItemAdapter(
    val list: MutableList<ItemTransactionDetail>
) :
    BaseBindingRecyclerAdapter<ItemTransactionDetail, RecyclerView.ViewHolder>(list) {
    override fun onCreateViewHolder(binding: ViewDataBinding): RecyclerView.ViewHolder {
        return TransactionDetailViewHolder(
            binding as ItemTransactionDetailBinding
        )
    }

    override fun getLayoutIdForViewType(viewType: Int): Int = R.layout.item_transaction_detail

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        if (holder is TransactionDetailViewHolder) {
            holder.onBind(position, list[position])
        }
    }
}
