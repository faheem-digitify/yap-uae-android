package co.yap.modules.dashboard.more.yapforyou.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.yap.yapuae.BR
import co.yap.yapuae.R
import co.yap.modules.dashboard.more.yapforyou.activities.YAPForYouActivity
import co.yap.modules.dashboard.more.yapforyou.interfaces.IYAPForYou
import co.yap.modules.dashboard.more.yapforyou.models.Achievement
import co.yap.modules.dashboard.more.yapforyou.viewmodels.YAPForYouViewModel
import co.yap.yapcore.interfaces.OnItemClickListener

class YAPForYouFragment : YapForYouBaseFragment<IYAPForYou.ViewModel>(), IYAPForYou.View {
    override fun getBindingVariable(): Int = BR.viewModel
    override fun getLayoutId(): Int = R.layout.fragment_yap_for_you

    override val viewModel: YAPForYouViewModel
        get() = ViewModelProviders.of(this).get(YAPForYouViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.parentViewModel?.getAchievements()
        addObservers()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycleView()
    }

    private fun setupRecycleView() {
        viewModel.adaptor.allowFullItemClickListener = true
        viewModel.adaptor.setItemListener(listener)
    }

    private val listener = object : OnItemClickListener {
        override fun onItemClick(view: View, data: Any, pos: Int) {
            if (data is Achievement) {
                viewModel.setSelectedAchievement(achievement = data)
                navigate(R.id.achievementFragment)
            }
        }
    }

    override fun addObservers() {
        viewModel.clickEvent.observe(this, clickObserver)
        viewModel.parentViewModel?.achievementsResponse?.observe(this, Observer {
            viewModel.setAchievements(it)
        })
    }

    private val clickObserver = Observer<Int> {
        when (it) {
            R.id.btnView -> {
                viewModel.state.currentAchievement.get()?.let { y4YAchievementData ->
                    viewModel.setSelectedAchievement(y4YAchievementData)
                    navigate(R.id.achievementFragment)
                }
            }
        }
    }

    override fun onDestroy() {
        viewModel.clickEvent.removeObservers(this)
        viewModel.parentViewModel?.achievementsResponse?.removeObservers(this)
        super.onDestroy()
    }

    override fun onToolBarClick(id: Int) {
        super.onToolBarClick(id)
        when (id) {
            R.id.ivLeftIcon -> (requireActivity() as YAPForYouActivity).onBackPressed()
            R.id.ivRightIcon -> {
                viewModel.state.toolbarVisibility.set(false)
                navigate(R.id.completedAchievementsFragment)
            }
        }
    }
}
