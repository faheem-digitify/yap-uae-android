package co.yap.modules.dashboard.more.yapforyou.interfaces

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import co.yap.modules.dashboard.more.yapforyou.adapters.YAPForYouAdapter
import co.yap.modules.dashboard.more.yapforyou.models.Achievement
import co.yap.networking.transactions.responsedtos.achievement.AchievementResponse
import co.yap.yapcore.IBase
import co.yap.yapcore.SingleClickEvent

interface IYAPForYou {
    interface State : IBase.State {
        var toolbarVisibility: ObservableBoolean
        var currentAchievement: ObservableField<Achievement>
        var isNoCompletedAchievements: ObservableBoolean

    }

    interface ViewModel : IBase.ViewModel<State> {
        var clickEvent: SingleClickEvent
        var adaptor: YAPForYouAdapter
        fun handlePressOnView(id: Int)
        fun setAchievements(achievementsResponse: ArrayList<AchievementResponse>)
        fun setSelectedAchievement(achievement: Achievement)
        fun getCurrentAchievement(from: ArrayList<Achievement>): Achievement?
    }

    interface View : IBase.View<ViewModel> {
        fun addObservers()
    }
}
