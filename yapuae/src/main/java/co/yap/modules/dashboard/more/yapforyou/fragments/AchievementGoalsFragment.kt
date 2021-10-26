package co.yap.modules.dashboard.more.yapforyou.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.yap.yapuae.R
import co.yap.yapuae.databinding.FragmentAchievementGoalsBinding
import co.yap.modules.dashboard.more.yapforyou.interfaces.IAchievementGoals
import co.yap.modules.dashboard.more.yapforyou.models.Achievement
import co.yap.modules.dashboard.more.yapforyou.models.YAPForYouGoal
import co.yap.modules.dashboard.more.yapforyou.viewmodels.AchievementViewModel
import co.yap.widgets.MultiStateView
import co.yap.yapcore.BR
import co.yap.yapcore.interfaces.OnItemClickListener

class AchievementGoalsFragment : YapForYouBaseFragment<IAchievementGoals.ViewModel>(),
    IAchievementGoals.View {
    override fun getBindingVariable(): Int = BR.viewModel
    override fun getLayoutId(): Int = R.layout.fragment_achievement_goals

    override val viewModel: AchievementViewModel
        get() = ViewModelProviders.of(this).get(AchievementViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addObservers()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdaptor()
    }

    private fun setupAdaptor() {
        viewModel.parentViewModel?.selectedAchievement?.get()?.let {
            setGoalsList(it)
        }
        viewModel.adapter.setItemListener(detailItemClickListener)
        viewModel.adapter.allowFullItemClickListener = true
    }

    private fun addObservers() {
        viewModel.parentViewModel?.achievementsResponse?.observe(this, Observer {
            val updatedSelectedAchievement =
                viewModel.parentViewModel?.achievementsList?.first { it.title == viewModel.parentViewModel?.selectedAchievement?.get()?.title }
            setGoalsList(updatedSelectedAchievement)
        })
    }

    private fun setGoalsList(achievement: Achievement?) {
        viewModel.parentViewModel?.selectedAchievement?.set(achievement)
        getBinding().multiStateView.viewState =
            if (achievement?.goals.isNullOrEmpty()) MultiStateView.ViewState.EMPTY else MultiStateView.ViewState.CONTENT
        achievement?.goals?.let { goals ->
            viewModel.adapter.setList(goals)
        }
    }

    private fun removeObservers() {
        viewModel.parentViewModel?.achievementsResponse?.removeObservers(this)
    }

    private val detailItemClickListener = object :
        OnItemClickListener {
        override fun onItemClick(view: View, data: Any, pos: Int) {
            if (data is YAPForYouGoal) {
                viewModel.setSelectedAchievementTask(YAPForYouGoal = data)
                navigate(R.id.achievementDetailFragment)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        removeObservers()
    }

    override fun getBinding(): FragmentAchievementGoalsBinding {
        return (viewDataBinding as FragmentAchievementGoalsBinding)
    }
}
