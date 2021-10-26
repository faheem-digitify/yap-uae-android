package co.yap.modules.location.kyc_additional_info.cardontheway

import android.animation.AnimatorSet
import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import androidx.core.animation.addListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import co.yap.modules.location.activities.LocationSelectionActivity
import co.yap.modules.location.fragments.LocationChildFragment
import co.yap.widgets.AnimatingProgressBar
import co.yap.widgets.video.ExoPlayerCallBack
import co.yap.yapcore.AdjustEvents.Companion.trackAdjustPlatformEvent
import co.yap.yapcore.BR
import co.yap.yapcore.R
import co.yap.yapcore.adjust.AdjustEvents
import co.yap.yapcore.constants.Constants
import co.yap.yapcore.helpers.AnimationUtils
import co.yap.yapcore.leanplum.KYCEvents
import co.yap.yapcore.leanplum.trackEvent
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Player.REPEAT_MODE_OFF
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import kotlinx.android.synthetic.main.fragment_card_on_the_way.*

class CardOnTheWayFragment : LocationChildFragment<ICardOnTheWay.ViewModel>(),
    ICardOnTheWay.View {

    private val windowSize: Rect = Rect()
    override fun getBindingVariable(): Int = BR.viewModel
    override fun getLayoutId(): Int = R.layout.fragment_card_on_the_way

    override val viewModel: CardOnTheWayViewModel by viewModels()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val display = activity?.windowManager?.defaultDisplay
        display?.getRectSize(windowSize)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        trackAdjustPlatformEvent(AdjustEvents.KYC_END.type)
        setObservers()
    }

    override fun onResume() {
        super.onResume()
        startAnimation()
        setCardAnimation()
    }

    override fun setObservers() {
        viewModel.clickEvent.observe(this, clickListener)
    }

    private val clickListener = Observer<Int> {
        when (it) {
            R.id.btnGoToDashboard -> {
                if (viewModel.parentViewModel?.isOnBoarding == true) trackEvent(KYCEvents.KYC_ORDERED.type)
                setIntentResult()
            }
        }
    }

    override fun setCardAnimation() {
        andExoPlayerView.setSource(R.raw.card_on_its_way)
        andExoPlayerView?.player?.repeatMode = REPEAT_MODE_OFF
        andExoPlayerView.setExoPlayerCallBack(object : ExoPlayerCallBack {
            override fun onError() {
                andExoPlayerView.setSource(R.raw.card_on_its_way)
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

    private fun startAnimation() {
        Handler(Looper.getMainLooper()).postDelayed({
            toolbarAnimation().apply {
                addListener(onEnd = {
                })
            }.start()
        }, 500)
    }

    private fun toolbarAnimation(): AnimatorSet {
        val checkButton =
            (activity as LocationSelectionActivity).findViewById<ImageView>(R.id.tbBtnCheck)
        val backButton =
            (activity as LocationSelectionActivity).findViewById<ImageView>(R.id.tbBtnBack)
        val progressbar =
            (activity as LocationSelectionActivity).findViewById<AnimatingProgressBar>(R.id.tbProgressBar)

        val checkBtnEndPosition = (windowSize.width() / 2) - (checkButton.width / 2)

        checkButton.isEnabled = true
        return AnimationUtils.runSequentially(
            AnimationUtils.pulse(checkButton),
            AnimationUtils.runTogether(
                AnimationUtils.fadeOut(backButton, 200),
                AnimationUtils.fadeOut(progressbar, 200)
            ),
            AnimationUtils.slideHorizontal(
                view = checkButton,
                from = checkButton.x,
                to = checkBtnEndPosition.toFloat(),
                duration = 500
            )
        )
    }

    private fun setIntentResult() {
        val intent = Intent()
        intent.putExtra(Constants.ADDRESS_SUCCESS, true)
        activity?.setResult(Activity.RESULT_OK, intent)
        activity?.finish()
    }

    override fun removeObservers() {
        viewModel.clickEvent.removeObservers(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        removeObservers()
    }

    override fun onBackPressed(): Boolean = true
}
