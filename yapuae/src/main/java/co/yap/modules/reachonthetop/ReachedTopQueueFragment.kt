package co.yap.modules.reachonthetop

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.yap.yapuae.BR
import co.yap.yapuae.R
import co.yap.modules.dashboard.main.activities.YapDashboardActivity
import co.yap.modules.kyc.activities.DocumentsDashboardActivity
import co.yap.modules.location.activities.LocationSelectionActivity
import co.yap.networking.cards.responsedtos.Address
import co.yap.translation.Strings
import co.yap.widgets.video.ExoPlayerCallBack
import co.yap.yapcore.AdjustEvents.Companion.trackAdjustPlatformEvent
import co.yap.yapcore.BaseBindingFragment
import co.yap.yapcore.adjust.AdjustEvents
import co.yap.yapcore.constants.Constants
import co.yap.yapcore.constants.RequestCodes
import co.yap.yapcore.firebase.FirebaseEvent
import co.yap.yapcore.firebase.trackEventWithScreenName
import co.yap.yapcore.helpers.extentions.ExtraType
import co.yap.yapcore.helpers.extentions.getValue
import co.yap.yapcore.helpers.extentions.launchActivity
import co.yap.yapcore.leanplum.KYCEvents
import co.yap.yapcore.leanplum.trackEvent
import co.yap.yapcore.managers.SessionManager
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Player.REPEAT_MODE_OFF
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import kotlinx.android.synthetic.main.fragment_reached_queue_top.*

class ReachedTopQueueFragment : BaseBindingFragment<IReachedQueueTop.ViewModel>(),
    IReachedQueueTop.View {
    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId() = R.layout.fragment_reached_queue_top

    override val viewModel: IReachedQueueTop.ViewModel
        get() = ViewModelProviders.of(this).get(ReachedQueueTopViewModel::class.java)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservers()
    }

    override fun onResume() {
        super.onResume()
        setCardAnimation()
    }

    override fun setObservers() {
        viewModel.clickEvent.observe(this, clickEvent)
    }

    override fun removeObservers() {
        viewModel.clickEvent.removeObservers(this)
    }


    override fun setCardAnimation() {
        andExoPlayerView.setSource(R.raw.yap_card_animation)
        andExoPlayerView?.player?.repeatMode = REPEAT_MODE_OFF
        andExoPlayerView.setExoPlayerCallBack(object : ExoPlayerCallBack {
            override fun onError() {
                andExoPlayerView.setSource(R.raw.yap_card_animation)
            }

            override fun onTracksChanged(
                trackGroups: TrackGroupArray,
                trackSelections: TrackSelectionArray
            ) {
            }

            override fun onPositionDiscontinuity(reason: Int) {
            }

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    andExoPlayerView.visibility = View.VISIBLE
                }
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
            }
        })
    }

    var clickEvent = Observer<Int> {
        when (it) {
            R.id.btnCompleteVerification -> {
                viewModel.completeVerification {
                    trackAdjustPlatformEvent(AdjustEvents.KYC_START.type)
                    trackEventWithScreenName(FirebaseEvent.COMPLETE_VERIFICATION)
                    launchActivity<DocumentsDashboardActivity>(requestCode = RequestCodes.REQUEST_KYC_DOCUMENTS) {
                        putExtra(Constants.name, viewModel.state.firstName)
                        putExtra(Constants.data, false)
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                RequestCodes.REQUEST_KYC_DOCUMENTS -> handleKYCRequestResult(data)
                RequestCodes.REQUEST_FOR_LOCATION -> handleLocationRequestResult(data)
            }
        } else {
            goToDashboard()
        }
    }

    override fun handleKYCRequestResult(data: Intent?) {
        data?.let {
            val success =
                data.getValue(
                    Constants.result,
                    ExtraType.BOOLEAN.name
                ) as? Boolean
            val skipped =
                data.getValue(
                    Constants.skipped,
                    ExtraType.BOOLEAN.name
                ) as? Boolean

            success?.let {
                if (it) {
                    startActivityForResult(
                        LocationSelectionActivity.newIntent(
                            context = requireContext(),
                            address = SessionManager.userAddress ?: Address(),
                            headingTitle = getString(Strings.screen_meeting_location_display_text_add_new_address_title),
                            subHeadingTitle = getString(Strings.screen_meeting_location_display_text_subtitle),
                            onBoarding = true
                        ), RequestCodes.REQUEST_FOR_LOCATION
                    )
                } else {
                    skipped?.let { skip ->
                        trackEvent(KYCEvents.SKIP_KYC.type)
                        goToDashboard()
                    }
                }
            }
        }
    }

    override fun handleLocationRequestResult(data: Intent?) {
        data?.let {
            if (it.getBooleanExtra(
                    Constants.ADDRESS_SUCCESS,
                    false
                )
            ) trackEvent(KYCEvents.KYC_ORDERED.type)
            goToDashboard()
        } ?: goToDashboard()
    }

    override fun goToDashboard() {
        SessionManager.sendFcmTokenToServer(requireContext()) {}
        startActivity(Intent(requireContext(), YapDashboardActivity::class.java))
        activity?.finishAffinity()
    }

    override fun onDestroy() {
        super.onDestroy()
        removeObservers()
    }

    override fun onBackPressed(): Boolean {
        activity?.finishAffinity()
        return true
    }
}