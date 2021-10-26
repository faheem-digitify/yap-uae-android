package co.yap.modules.dashboard.more.yapforyou.viewmodels

import android.app.Application
import co.yap.modules.dashboard.more.yapforyou.adapters.YAPForYouAdapter
import co.yap.modules.dashboard.more.yapforyou.interfaces.IYAPForYou
import co.yap.modules.dashboard.more.yapforyou.models.Achievement
import co.yap.modules.dashboard.more.yapforyou.states.YAPForYouState
import co.yap.networking.interfaces.IRepositoryHolder
import co.yap.networking.transactions.TransactionsRepository
import co.yap.networking.transactions.responsedtos.achievement.AchievementResponse
import co.yap.translation.Strings
import co.yap.yapcore.SingleClickEvent

class YAPForYouViewModel(application: Application) :
    YapForYouBaseViewModel<IYAPForYou.State>(application), IYAPForYou.ViewModel,
    IRepositoryHolder<TransactionsRepository> {

    override val repository: TransactionsRepository = TransactionsRepository
    override val state: YAPForYouState = YAPForYouState()
    override var clickEvent: SingleClickEvent = SingleClickEvent()
    override var adaptor: YAPForYouAdapter = YAPForYouAdapter(mutableListOf())

    override fun handlePressOnView(id: Int) {
        clickEvent.setValue(id)
    }

    override fun onCreate() {
        super.onCreate()
        setToolbarData()
    }

    private fun setToolbarData() {
        state.toolbarTitle = getString(Strings.screen_yap_for_you_display_text_title)
        toggleToolBarVisibility(false)
        state.toolbarVisibility.set(true)
    }

    override fun setAchievements(achievementsResponse: ArrayList<AchievementResponse>) {
        adaptor.setList(parentViewModel?.achievementsList ?: mutableListOf())
        state.currentAchievement.set(getCurrentAchievement(parentViewModel?.achievementsList as ArrayList<Achievement>))
        state.isNoCompletedAchievements.set(parentViewModel?.achievementsList?.filter { it.isCompleted }
            .isNullOrEmpty())
    }

    override fun setSelectedAchievement(achievement: Achievement) {
        parentViewModel?.selectedAchievement?.set(achievement)
    }

    override fun getCurrentAchievement(from: ArrayList<Achievement>): Achievement? {
        from.sortWith(Comparator { second, first ->
            if (first.completedPercentage > second.completedPercentage) {
                1
            } else if (first.completedPercentage == second.completedPercentage) {
                if (first.lastUpdated > second.lastUpdated) 1 else if (first.lastUpdated == second.lastUpdated) 0 else -1
            } else {
                -1
            }
        })
        return from.firstOrNull {
            it.completedPercentage != 100
        }
    }
}
