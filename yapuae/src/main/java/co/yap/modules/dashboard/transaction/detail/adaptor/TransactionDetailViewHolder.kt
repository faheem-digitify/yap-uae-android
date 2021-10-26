package co.yap.modules.dashboard.transaction.detail.adaptor

import androidx.recyclerview.widget.RecyclerView
import co.yap.yapuae.databinding.ItemTransactionDetailBinding
import co.yap.modules.dashboard.transaction.detail.models.ItemTransactionDetail

class TransactionDetailViewHolder(val binding: ItemTransactionDetailBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun onBind(
        position: Int,
        item: ItemTransactionDetail
    ) {
        binding.viewModel =
            TransactionDetailItemViewModel(
                item,
                position
            )
        binding.executePendingBindings()
    }
}