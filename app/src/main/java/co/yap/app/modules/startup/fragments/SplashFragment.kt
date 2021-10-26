package co.yap.app.modules.startup.fragments

import android.animation.Animator
import android.animation.AnimatorSet
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import co.yap.app.BR
import co.yap.app.BuildConfig
import co.yap.app.R
import co.yap.app.main.MainChildFragment
import co.yap.app.modules.startup.interfaces.ISplash
import co.yap.app.modules.startup.viewmodels.SplashViewModel
import co.yap.yapcore.animations.animators.ScaleAnimator
import co.yap.yapcore.constants.Constants.KEY_IMAGE_LOADING_TIME
import co.yap.yapcore.constants.Constants.KEY_IS_FIRST_TIME_USER
import co.yap.yapcore.helpers.SharedPreferenceManager
import co.yap.yapcore.helpers.Utils
import co.yap.yapcore.helpers.alert
import co.yap.yapcore.helpers.extentions.openPlayStore
import kotlinx.android.synthetic.main.fragment_splash.*

class SplashFragment : MainChildFragment<ISplash.ViewModel>(), ISplash.View {
    private var animatorSet: AnimatorSet? = null

    override fun getBindingVariable() = BR.viewModel
    override fun getLayoutId() = R.layout.fragment_splash

    override val viewModel: SplashViewModel
        get() = ViewModelProviders.of(this).get(SplashViewModel::class.java)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        animatorSet = AnimatorSet()
        viewModel.splashComplete.observe(this, Observer {
            if (it) viewModel.getAppUpdate()
        })
        SharedPreferenceManager.getInstance(requireContext()).save(
            KEY_IMAGE_LOADING_TIME,
            System.currentTimeMillis().toString()
        )
        viewModel.appUpdate.observe(this, Observer {
            if (it != null && it.androidForceUpdate && Utils.checkForUpdate(
                    BuildConfig.VERSION_NAME,
                    it.androidAppVersionNumber
                )
            ) {
                requireContext().alert(
                    getString(R.string.screen_splash_display_text_force_update),
                    getString(R.string.screen_splash_button_force_update),
                    getString(R.string.screen_splash_button_force_update),
                    false
                ) {
                    requireContext().openPlayStore()
                    requireActivity().finish()
                }
            } else {
                playAnimationAndMoveNext()
            }
        })
    }

    private fun playAnimationAndMoveNext() {
        val scaleLogo =
            ScaleAnimator(1.0f, 150.0f, AccelerateDecelerateInterpolator()).with(ivLogo, 1500)
        val scaleDot =
            ScaleAnimator(1.0f, 150.0f, AccelerateDecelerateInterpolator()).with(ivDot, 1500)
        scaleDot.startDelay = 400

        animatorSet?.play(scaleLogo)?.with(scaleDot)
        animatorSet?.interpolator = AccelerateDecelerateInterpolator()
        animatorSet?.start()

        animatorSet?.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {}
            override fun onAnimationEnd(animation: Animator?) {
                moveNext()
            }

            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationStart(animation: Animator?) {}

        })
    }

    private fun moveNext() {
        if (SharedPreferenceManager.getInstance(requireContext()).getValueBoolien(
                KEY_IS_FIRST_TIME_USER,
                true
            )
        ) {
            SharedPreferenceManager.getInstance(requireContext()).save(
                KEY_IS_FIRST_TIME_USER,
                false
            )
            findNavController().navigate(R.id.action_splashFragment_to_accountSelectionFragment)
        } else {
            findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        animatorSet?.resume()
    }

    override fun onPause() {
        super.onPause()
        animatorSet?.pause()
    }

    override fun onDestroyView() {
        animatorSet?.cancel()
        animatorSet = null
        viewModel.splashComplete.removeObservers(this)
        super.onDestroyView()
    }
}