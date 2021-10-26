package co.yap.modules.dashboard.more.yapforyou.interfaces

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import co.yap.modules.dashboard.more.yapforyou.models.Achievement
import co.yap.modules.dashboard.more.yapforyou.models.YAPForYouGoal
import co.yap.networking.transactions.responsedtos.achievement.AchievementResponse
import co.yap.yapcore.IBase
import co.yap.yapcore.SingleClickEvent

interface IYapForYouMain {

    interface View : IBase.View<ViewModel>

    interface ViewModel : IBase.ViewModel<State> {
        var clickEvent: SingleClickEvent
        var selectedAchievement: ObservableField<Achievement?>
        var selectedAchievementGoal: ObservableField<YAPForYouGoal?>
        var achievementsList: MutableList<Achievement>
        var achievementsResponse: MutableLiveData<ArrayList<AchievementResponse>>
        fun handlePressButton(id: Int)
        fun getAchievements()
        fun getMockApiResponse()
    }

    interface State : IBase.State {
        var toolbarVisibility: ObservableBoolean
        var leftIcon: ObservableField<Int>
        var leftIconVisibility: ObservableBoolean
    }
}
