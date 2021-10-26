package co.yap.modules.dashboard.more.yapforyou.adapters

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import co.yap.yapuae.R
import co.yap.yapuae.databinding.ItemGoalAchievementBinding
import co.yap.modules.dashboard.more.yapforyou.itemviewmodels.AchievementGoalItemViewModel
import co.yap.modules.dashboard.more.yapforyou.models.YAPForYouGoal
import co.yap.yapcore.BaseBindingRecyclerAdapter

class AchievementGoalAdaptor(private val list: MutableList<YAPForYouGoal>) :
    BaseBindingRecyclerAdapter<YAPForYouGoal, AchievementGoalAdaptor.AchievementTaskViewHolder>(
        list
    ) {

    override fun getLayoutIdForViewType(viewType: Int): Int = R.layout.item_goal_achievement

    override fun onCreateViewHolder(binding: ViewDataBinding): AchievementTaskViewHolder {
        return AchievementTaskViewHolder(binding as ItemGoalAchievementBinding)
    }

    override fun onBindViewHolder(holder: AchievementTaskViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.onBind(list[position])
    }

    class AchievementTaskViewHolder(private val itemAchievementBinding: ItemGoalAchievementBinding) :
        RecyclerView.ViewHolder(itemAchievementBinding.root) {

        fun onBind(task: YAPForYouGoal) {
            itemAchievementBinding.viewModel =
                AchievementGoalItemViewModel(
                    task
                )
            itemAchievementBinding.executePendingBindings()
        }
    }
}
