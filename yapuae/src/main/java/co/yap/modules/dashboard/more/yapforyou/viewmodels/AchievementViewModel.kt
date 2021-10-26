package co.yap.modules.dashboard.more.yapforyou.viewmodels

import android.app.Application
import co.yap.yapuae.R
import co.yap.modules.dashboard.more.yapforyou.adapters.AchievementGoalAdaptor
import co.yap.modules.dashboard.more.yapforyou.interfaces.IAchievementGoals
import co.yap.modules.dashboard.more.yapforyou.models.YAPForYouGoal
import co.yap.modules.dashboard.more.yapforyou.states.AchievementGoalsState
import co.yap.translation.Strings
import co.yap.yapcore.SingleClickEvent

class AchievementViewModel(application: Application) :
    YapForYouBaseViewModel<IAchievementGoals.State>(application),
    IAchievementGoals.ViewModel {

    override val state: AchievementGoalsState = AchievementGoalsState()
    override var clickEvent: SingleClickEvent = SingleClickEvent()
    override val adapter: AchievementGoalAdaptor = AchievementGoalAdaptor(mutableListOf())
    override fun handlePressOnButton(id: Int) {
        clickEvent.setValue(id)
    }

    override fun setSelectedAchievementTask(YAPForYouGoal: YAPForYouGoal?) {
        parentViewModel?.selectedAchievementGoal?.set(YAPForYouGoal)
    }

    override fun onCreate() {
        super.onCreate()
        setupToolbar()
    }

    private fun setupToolbar() {
        setToolBarTitle(getString(Strings.screen_yap_for_you_display_text_title))
        setLeftIcon(R.drawable.ic_back_arrow_left)
        setLeftIconVisibility(true)
        toggleToolBarVisibility(true)
    }
}
