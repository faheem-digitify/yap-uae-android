package co.yap.modules.onboarding.fragments

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.databinding.Observable
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import co.yap.yapuae.BR
import co.yap.yapuae.R
import co.yap.modules.autoreadsms.MySMSBroadcastReceiver
import co.yap.modules.onboarding.activities.CreatePasscodeActivity
import co.yap.modules.onboarding.interfaces.IPhoneVerification
import co.yap.modules.onboarding.viewmodels.PhoneVerificationViewModel
import co.yap.yapcore.constants.Constants
import co.yap.yapcore.constants.RequestCodes
import co.yap.yapcore.firebase.FirebaseEvent
import co.yap.yapcore.firebase.trackEventWithScreenName
import co.yap.yapcore.helpers.extentions.getOtpFromMessage
import co.yap.yapcore.helpers.extentions.launchActivity
import co.yap.yapcore.helpers.extentions.startSmsConsent
import co.yap.yapcore.leanplum.SignupEvents
import co.yap.yapcore.leanplum.trackEvent
import com.google.android.gms.auth.api.phone.SmsRetriever

class PhoneVerificationFragment : OnboardingChildFragment<IPhoneVerification.ViewModel>(),
    IPhoneVerification.View {
    private var intentFilter: IntentFilter? = null
    private var appSMSBroadcastReceiver: MySMSBroadcastReceiver? = null

    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.fragment_phone_verification

    override val viewModel: PhoneVerificationViewModel
        get() = ViewModelProviders.of(this).get(PhoneVerificationViewModel::class.java)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.state.reverseTimer(10, requireContext())
        requireContext().startSmsConsent()
        initBroadcast()
        viewModel.state.otp.addOnPropertyChangedCallback(stateObserverOtp)
        viewModel.nextButtonPressEvent.observe(this, clickEvent)
    }

    override fun onResume() {
        super.onResume()
        requireContext().registerReceiver(
            appSMSBroadcastReceiver,
            intentFilter,
            SmsRetriever.SEND_PERMISSION,
            null
        )
    }

    override fun onPause() {
        super.onPause()
        requireContext().unregisterReceiver(appSMSBroadcastReceiver)
    }

    private val clickEvent = Observer<Int> { id ->
        when (id) {
            R.id.btnResend -> {
                requireContext().startSmsConsent()
            }
            R.id.done -> {
                viewModel.verifyOtp {
                    trackEventWithScreenName(FirebaseEvent.VERIFY_NUMBER)
                    launchActivity<CreatePasscodeActivity>(
                        requestCode = RequestCodes.REQUEST_CODE_CREATE_PASSCODE
                    )
                }
            }

        }
    }

    private val stateObserverOtp = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            if (viewModel.isValidOtpLength(viewModel.state.otp.get() ?: "")) {
                viewModel.nextButtonPressEvent.setValue(R.id.done)
            }
        }
    }

    private fun initBroadcast() {
        intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        appSMSBroadcastReceiver =
            MySMSBroadcastReceiver(object : MySMSBroadcastReceiver.OnSmsReceiveListener {
                override fun onReceive(code: Intent?) {
                    code?.let {
                        it.resolveActivity(requireContext().packageManager)?.run {
                            if (packageName == "com.google.android.gms" && className == "com.google.android.gms.auth.api.phone.ui.UserConsentPromptActivity")
                                startActivityForResult(it, Constants.SMS_CONSENT_REQUEST)
                        }
                    }
                }
            })
    }

    override fun onDestroyView() {
        viewModel.nextButtonPressEvent.removeObservers(this)
        viewModel.state.otp.removeOnPropertyChangedCallback(stateObserverOtp)
        super.onDestroyView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RequestCodes.REQUEST_CODE_CREATE_PASSCODE -> {
                if (null != data) {
                    //trackEvent(TrackEvents.OTP_CODE_ENTERED)
                    viewModel.setPasscode(data.getStringExtra("PASSCODE") ?: "")
                    findNavController().navigate(R.id.action_phoneVerificationFragment_to_nameFragment)
                    trackEventWithScreenName(FirebaseEvent.CREATE_PIN)
                    trackEvent(SignupEvents.SIGN_UP_PASSCODE_CREATED.type)
                }
            }
            Constants.SMS_CONSENT_REQUEST ->
                if (resultCode == Activity.RESULT_OK) {
                    data?.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE).also {
                        viewModel.state.otp.set(it?.getOtpFromMessage() ?: "")
                    }
                }
        }
    }
}
