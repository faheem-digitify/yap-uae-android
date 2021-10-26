package co.yap.modules.dashboard.more.yapforyou.interfaces

import androidx.databinding.ObservableField
import co.yap.yapuae.databinding.FragmentAchievementGoalsBinding
import co.yap.modules.dashboard.more.yapforyou.adapters.AchievementGoalAdaptor
import co.yap.modules.dashboard.more.yapforyou.models.YAPForYouGoal
import co.yap.yapcore.IBase
import co.yap.yapcore.SingleClickEvent

interface IAchievementGoals {

    interface View : IBase.View<ViewModel> {
        fun getBinding(): FragmentAchievementGoalsBinding
    }

    interface ViewModel : IBase.ViewModel<State> {
        var clickEvent: SingleClickEvent
        val adapter: AchievementGoalAdaptor
        fun handlePressOnButton(id: Int)
        fun setSelectedAchievementTask(YAPForYouGoal: YAPForYouGoal?)
    }

    interface State : IBase.State {
        var achievementIcon: ObservableField<Int>
    }
}
