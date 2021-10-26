package co.yap.modules.dashboard.more.yapforyou.achievementdetail

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.yap.yapuae.BR
import co.yap.yapuae.R
import co.yap.yapuae.databinding.FragmentAchievementGoalDetailsBinding
import co.yap.modules.dashboard.cards.paymentcarddetail.activities.PaymentCardDetailActivity
import co.yap.modules.dashboard.more.main.activities.MoreActivity
import co.yap.modules.dashboard.more.yapforyou.fragments.YapForYouBaseFragment
import co.yap.modules.dashboard.yapit.addmoney.main.AddMoneyActivity
import co.yap.repositories.InviteFriendRepository
import co.yap.translation.Strings
import co.yap.yapcore.enums.FeatureSet
import co.yap.yapcore.enums.YAPForYouGoalAction
import co.yap.yapcore.enums.YapForYouGoalType
import co.yap.yapcore.helpers.Utils
import co.yap.yapcore.helpers.extentions.launchActivity
import co.yap.yapcore.helpers.extentions.launchActivityForResult
import co.yap.yapcore.helpers.extentions.share
import co.yap.yapcore.managers.SessionManager

class AchievementGoalDetailFragment : YapForYouBaseFragment<IAchievementGoalDetail.ViewModel>(),
    IAchievementGoalDetail.View {
    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.fragment_achievement_goal_details
    override val viewModel: AchievementGoalDetailViewModel
        get() = ViewModelProviders.of(this).get(AchievementGoalDetailViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addObservers()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (viewModel.parentViewModel?.selectedAchievementGoal?.get()?.title == YapForYouGoalType.FREEZE_UNFREEZE_CARD.title) {
            val animationDrawable: AnimationDrawable =
                getBindings().tvFreezeAnimationView.background as AnimationDrawable
            animationDrawable.setEnterFadeDuration(1000)
            animationDrawable.setExitFadeDuration(1500)
            animationDrawable.isOneShot = true
            animationDrawable.start()
        }
    }

    private val onClickObserver = Observer<Int> {
        val action = viewModel.parentViewModel?.selectedAchievementGoal?.get()?.action
        when (it) {
            R.id.btnAction -> {
                when (action) {
                    is YAPForYouGoalAction.Button -> {
                        performActionOnClick(controller = action.controllerOnAction ?: "")
                    }
                    is YAPForYouGoalAction.None -> {
                    }
                }
            }
        }
    }

    private fun performActionOnClick(controller: String) {
        when (controller) {
            YapForYouGoalType.INVITE_FRIEND.name -> {
                requireContext().share(
                    text = getString(
                        Strings.screen_invite_friend_display_text_share_url,
                        Utils.getAdjustURL()
                    )
                )
                InviteFriendRepository().inviteAFriend {
                    viewModel.parentViewModel?.getAchievements()
                }
            }

            AddMoneyActivity::class.simpleName -> {
                launchActivityForResult<AddMoneyActivity>(type = FeatureSet.TOP_UP) { _, _ ->
                    viewModel.parentViewModel?.getAchievements()
                }
            }

            MoreActivity::javaClass.name -> {
                launchActivityForResult<MoreActivity> { _, _ ->
                    viewModel.parentViewModel?.getAchievements()
                }
            }
            PaymentCardDetailActivity::class.simpleName -> {
                SessionManager.getPrimaryCard()?.let { debitCard ->
                    launchActivity<PaymentCardDetailActivity>(
                        requestCode = 9999,
                        type = FeatureSet.DEBIT_CARD_DETAIL
                    ) {
                        putExtra(PaymentCardDetailActivity.CARD, debitCard)
                    }
                }
            }
        }
    }

    override fun addObservers() {
        viewModel.clickEvent.observe(this, onClickObserver)
        viewModel.parentViewModel?.achievementsResponse?.observe(this, Observer {
            val updatedYapForYouGoal =
                viewModel.parentViewModel?.achievementsList?.flatMap { it.goals!!.toList() }
                    ?.firstOrNull {
                        it.title == viewModel.parentViewModel?.selectedAchievementGoal?.get()?.title
                    }

            viewModel.parentViewModel?.selectedAchievementGoal?.set(updatedYapForYouGoal)
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel.parentViewModel?.getAchievements()
    }

    override fun removeObservers() {
        viewModel.clickEvent.removeObserver(onClickObserver)
        viewModel.parentViewModel?.achievementsResponse?.removeObservers(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        removeObservers()
    }

    private fun getBindings(): FragmentAchievementGoalDetailsBinding =
        viewDataBinding as FragmentAchievementGoalDetailsBinding

}