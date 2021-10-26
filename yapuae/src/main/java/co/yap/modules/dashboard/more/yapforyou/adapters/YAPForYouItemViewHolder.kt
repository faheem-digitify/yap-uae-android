package co.yap.modules.dashboard.more.yapforyou.adapters

import androidx.recyclerview.widget.RecyclerView
import co.yap.yapuae.databinding.ItemYapForYouBinding
import co.yap.modules.dashboard.more.yapforyou.itemviewmodels.YAPForYouItemViewModel
import co.yap.modules.dashboard.more.yapforyou.models.Achievement
import co.yap.yapcore.interfaces.OnItemClickListener

class YAPForYouItemViewHolder(private val itemYapForYouBinding: ItemYapForYouBinding) :
    RecyclerView.ViewHolder(itemYapForYouBinding.root) {

    fun onBind(
        position: Int,
        achievements: Achievement,
        onItemClickListener: OnItemClickListener?
    ) {

        itemYapForYouBinding.viewModel =
            YAPForYouItemViewModel(
                achievement = achievements,
                position = position,
                onItemClickListener = onItemClickListener
            )
        itemYapForYouBinding.executePendingBindings()

    }
}