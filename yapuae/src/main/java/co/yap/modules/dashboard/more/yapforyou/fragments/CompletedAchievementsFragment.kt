package co.yap.modules.dashboard.more.yapforyou.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import co.yap.yapuae.R
import co.yap.yapuae.databinding.FragmentCompletedAchievementsBinding
import co.yap.modules.dashboard.more.yapforyou.interfaces.ICompletedAchievements
import co.yap.modules.dashboard.more.yapforyou.models.Achievement
import co.yap.modules.dashboard.more.yapforyou.viewmodels.CompletedAchievementsViewModel
import co.yap.yapcore.BR
import co.yap.yapcore.interfaces.OnItemClickListener

class CompletedAchievementsFragment : YapForYouBaseFragment<ICompletedAchievements.ViewModel>(),
    ICompletedAchievements.View {
    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.fragment_completed_achievements

    override val viewModel: ICompletedAchievements.ViewModel
        get() = ViewModelProviders.of(this).get(CompletedAchievementsViewModel::class.java)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.adapter.setItemListener(itemClickListener)
        viewModel.adapter.allowFullItemClickListener = true

    }

    private val itemClickListener = object :
        OnItemClickListener {
        override fun onItemClick(view: View, data: Any, pos: Int) {
            if (data is Achievement) {
                viewModel.setSelectedAchievement(achievement = data)
                navigate(R.id.achievementFragment)
            }
        }
    }

    override fun getBinding(): FragmentCompletedAchievementsBinding {
        return viewDataBinding as FragmentCompletedAchievementsBinding
    }

}