package co.yap.modules.dashboard.more.yapforyou.states

import androidx.databinding.ObservableField
import co.yap.modules.dashboard.more.yapforyou.interfaces.IAchievementGoals
import co.yap.yapcore.BaseState

class AchievementGoalsState : BaseState(), IAchievementGoals.State {
    override var achievementIcon: ObservableField<Int> = ObservableField(-1)
}
