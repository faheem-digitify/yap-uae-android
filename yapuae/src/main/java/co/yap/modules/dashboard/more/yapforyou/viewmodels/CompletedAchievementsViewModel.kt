package co.yap.modules.dashboard.more.yapforyou.viewmodels

import android.app.Application
import co.yap.yapuae.R
import co.yap.modules.dashboard.more.yapforyou.adapters.CompletedAchievementsAdaptor
import co.yap.modules.dashboard.more.yapforyou.interfaces.ICompletedAchievements
import co.yap.modules.dashboard.more.yapforyou.models.Achievement
import co.yap.modules.dashboard.more.yapforyou.states.CompletedAchievementsState
import co.yap.translation.Strings
import co.yap.yapcore.SingleClickEvent

class CompletedAchievementsViewModel(application: Application) :
    YapForYouBaseViewModel<ICompletedAchievements.State>(application),
    ICompletedAchievements.ViewModel {
    override val state: CompletedAchievementsState = CompletedAchievementsState()
    override var clickEvent: SingleClickEvent = SingleClickEvent()
    override val adapter: CompletedAchievementsAdaptor =
        CompletedAchievementsAdaptor(mutableListOf())

    override fun onCreate() {
        super.onCreate()
        setupToolbar()
        val list = parentViewModel?.achievementsList ?: mutableListOf()
        adapter.setList(list.filter { it.isCompleted })
    }

    private fun setupToolbar() {
        toggleToolBarVisibility(true)
        setToolBarTitle(getString(Strings.screen_your_achievements_display_text_toolbar_title))
        setLeftIcon(R.drawable.ic_back_arrow_left)
        setLeftIconVisibility(true)
    }

    override fun setSelectedAchievement(achievement: Achievement) {
        parentViewModel?.selectedAchievement?.set(achievement)
    }

    override fun handlePressOnButton(id: Int) {
        clickEvent.setValue(id)
    }
}