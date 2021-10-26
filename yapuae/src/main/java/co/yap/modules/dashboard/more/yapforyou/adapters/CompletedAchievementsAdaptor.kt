package co.yap.modules.dashboard.more.yapforyou.adapters

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import co.yap.yapuae.R
import co.yap.yapuae.databinding.ItemCompletedAchievementBinding
import co.yap.modules.dashboard.more.yapforyou.itemviewmodels.CompletedAchievementItemViewModel
import co.yap.modules.dashboard.more.yapforyou.models.Achievement
import co.yap.yapcore.BaseBindingRecyclerAdapter

class CompletedAchievementsAdaptor(val list: MutableList<Achievement>) :
    BaseBindingRecyclerAdapter<Achievement, CompletedAchievementsAdaptor.CompletedItemViewHolder>(
        list
    ) {

    override fun getLayoutIdForViewType(viewType: Int): Int = R.layout.item_completed_achievement

    override fun onCreateViewHolder(binding: ViewDataBinding): CompletedItemViewHolder {
        return CompletedItemViewHolder(binding as ItemCompletedAchievementBinding)
    }

    override fun onBindViewHolder(holder: CompletedItemViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.onBind(list[position])
    }

    class CompletedItemViewHolder(private val itemCompletedAchievementBinding: ItemCompletedAchievementBinding) :
        RecyclerView.ViewHolder(itemCompletedAchievementBinding.root) {

        fun onBind(achievement: Achievement) {
            itemCompletedAchievementBinding.viewModel =
                CompletedAchievementItemViewModel(achievement)
            itemCompletedAchievementBinding.executePendingBindings()
        }
    }
}