package co.yap.modules.auth.otpverification

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.databinding.Observable
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.yap.yapuae.BR
import co.yap.yapuae.R
import co.yap.modules.auth.otpblocked.OtpBlockedInfoFragment
import co.yap.modules.autoreadsms.MySMSBroadcastReceiver
import co.yap.modules.onboarding.enums.AccountType
import co.yap.modules.onboarding.fragments.WaitingListFragment
import co.yap.modules.reachonthetop.ReachedTopQueueFragment
import co.yap.networking.customers.responsedtos.AccountInfo
import co.yap.yapcore.BaseBindingFragment
import co.yap.yapcore.constants.Constants.SMS_CONSENT_REQUEST
import co.yap.yapcore.firebase.FirebaseEvent
import co.yap.yapcore.firebase.trackEventWithScreenName
import co.yap.yapcore.helpers.SharedPreferenceManager
import co.yap.yapcore.helpers.TourGuideManager
import co.yap.yapcore.helpers.Utils
import co.yap.yapcore.helpers.biometric.BiometricUtil
import co.yap.yapcore.helpers.extentions.getOtpFromMessage
import co.yap.yapcore.helpers.extentions.startFragment
import co.yap.yapcore.helpers.extentions.startSmsConsent
import co.yap.yapcore.leanplum.SignInEvents
import co.yap.yapcore.leanplum.trackEvent
import co.yap.yapcore.managers.SessionManager
import com.google.android.gms.auth.api.phone.SmsRetriever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PhoneVerificationSignInFragment :
    BaseBindingFragment<IPhoneVerificationSignIn.ViewModel>(), IPhoneVerificationSignIn.View {
    private var intentFilter: IntentFilter? = null
    private var appSMSBroadcastReceiver: MySMSBroadcastReceiver? = null
    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.fragment_login_phone_verification

    override val viewModel: PhoneVerificationSignInViewModel
        get() = ViewModelProviders.of(this).get(PhoneVerificationSignInViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setObservers()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.state.reverseTimer(10, requireContext())
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        requireActivity().window.clearFlags(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        getData()
    }

    private val stateObserver = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            if (viewModel.isValidOtpLength(viewModel.state.otp.get() ?: "")) {
                viewModel.clickEvent.call()
            }
        }
    }

    override fun setObservers() {
        viewModel.state.otp.addOnPropertyChangedCallback(stateObserver)
        context?.startSmsConsent()
        initBroadcast()
        requireContext().registerReceiver(
            appSMSBroadcastReceiver,
            intentFilter,
            SmsRetriever.SEND_PERMISSION,
            null
        )
        viewModel.clickEvent.observe(this, Observer {
            viewModel.verifyOtp()
        })
        viewModel.postDemographicDataResult.observe(this, postDemographicDataObserver)
        viewModel.accountInfo.observe(this, onFetchAccountInfo)
    }

    private fun initBroadcast() {
        intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        appSMSBroadcastReceiver =
            MySMSBroadcastReceiver(object : MySMSBroadcastReceiver.OnSmsReceiveListener {
                override fun onReceive(code: Intent?) {
                    code?.let {
                        it.resolveActivity(requireContext().packageManager)?.run {
                            if (packageName == "com.google.android.gms" && className == "com.google.android.gms.auth.api.phone.ui.UserConsentPromptActivity"
                            )
                                startActivityForResult(it, SMS_CONSENT_REQUEST)
                        }
                    }
                }
            })
    }

    private val postDemographicDataObserver = Observer<Boolean> {
        viewModel.getAccountInfo()
    }
    private val onFetchAccountInfo = Observer<AccountInfo> {
        if (!it.isWaiting) {
            if (it.iban.isNullOrBlank()) {
                startFragment(
                    fragmentName = ReachedTopQueueFragment::class.java.name,
                    clearAllPrevious = true
                )
            } else {
                getCardAndTourInfo(it)
            }
        } else {
            startFragment(
                fragmentName = WaitingListFragment::class.java.name,
                clearAllPrevious = true
            )
        }
    }

    private fun getCardAndTourInfo(accountInfo: AccountInfo?) {
        accountInfo?.run {
            trackEventWithScreenName(FirebaseEvent.SIGN_IN_PIN)
            TourGuideManager.getTourGuides()
            SessionManager.getDebitCard { card ->
                SessionManager.updateCardBalance { }
                if (accountType == AccountType.B2C_HOUSEHOLD.name) {
                    val bundle = Bundle()
                    SharedPreferenceManager.getInstance(requireContext())
                        .setThemeValue(co.yap.yapcore.constants.Constants.THEME_HOUSEHOLD)
//                    bundle.putBoolean(OnBoardingHouseHoldActivity.EXISTING_USER, false)
//                    bundle.putParcelable(OnBoardingHouseHoldActivity.USER_INFO, accountInfo)
//                    startActivity(OnBoardingHouseHoldActivity.getIntent(requireContext(), bundle))
                    activity?.finish()
                } else {
                    if (BiometricUtil.hasBioMetricFeature(requireActivity())
                    ) {
                        SharedPreferenceManager.getInstance(requireContext()).save(
                            co.yap.yapcore.constants.Constants.KEY_IS_FINGERPRINT_PERMISSION_SHOWN,
                            true
                        )
                        if (SharedPreferenceManager.getInstance(requireContext()).getValueBoolien(
                                co.yap.yapcore.constants.Constants.KEY_TOUCH_ID_ENABLED,
                                false
                            )
                        ) {
                            if (accountInfo.otpBlocked == true || SessionManager.user?.freezeInitiator != null)
                                startFragment(fragmentName = OtpBlockedInfoFragment::class.java.name)
                            else {
                                SessionManager.sendFcmTokenToServer(requireContext()) {}
                                if (!this.isWaiting) {
                                    if (this.iban.isNullOrBlank()) {
                                        startFragment(
                                            fragmentName = ReachedTopQueueFragment::class.java.name,
                                            clearAllPrevious = true
                                        )

                                    } else {
//                                        findNavController().navigate(R.id.action_goto_yapDashboardActivity)
                                    }
                                } else {
                                    startFragment(
                                        fragmentName = WaitingListFragment::class.java.name,
                                        clearAllPrevious = true
                                    )
                                }

                            }
                            activity?.finish()
                        } else {
//                            val action =
//                                PhoneVerificationSignInFragmentDirections.actionPhoneVerificationSignInFragmentToSystemPermissionFragment(
//                                    Constants.TOUCH_ID_SCREEN_TYPE
//                                )
//                            findNavController().navigate(action)
                        }

                    } else {
                        if (accountInfo.otpBlocked == true || SessionManager.user?.freezeInitiator != null) {
                            startFragment(fragmentName = OtpBlockedInfoFragment::class.java.name)
                        } else {
                            SessionManager.sendFcmTokenToServer(requireContext()) {}
                            if (!this.isWaiting) {
                                if (this.iban.isNullOrBlank()) {
                                    startFragment(
                                        fragmentName = ReachedTopQueueFragment::class.java.name,
                                        clearAllPrevious = true
                                    )

                                } else {
                                    trackEvent(SignInEvents.SIGN_IN.type)
//                                    findNavController().navigate(R.id.action_goto_yapDashboardActivity)
                                }
                            } else {
                                startFragment(
                                    fragmentName = WaitingListFragment::class.java.name,
                                    clearAllPrevious = true
                                )
                            }
                        }
                        activity?.finish()
                    }
                }
            }
        }
    }

    private fun getData() {
//        viewModel.state.username =
//            arguments?.let { PhoneVerificationSignInFragmentArgs.fromBundle(it).username } as String
//        viewModel.state.passcode =
//            arguments?.let { PhoneVerificationSignInFragmentArgs.fromBundle(it).passcode } as String
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SMS_CONSENT_REQUEST ->
                if (resultCode == Activity.RESULT_OK) {
                    data?.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE).also {
                        viewModel.state.otp.set(it?.getOtpFromMessage() ?: "")
                    }
                }
        }
    }

    override fun removeObservers() {
        viewModel.state.otp.removeOnPropertyChangedCallback(stateObserver)
        viewModel.postDemographicDataResult.removeObservers(this)
        viewModel.accountInfo.removeObservers(this)
        viewModel.clickEvent.removeObservers(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        removeObservers()
    }

    override fun onToolBarClick(id: Int) {
        when (id) {
            R.id.ivLeftIcon -> {
                Utils.hideKeyboard(requireView())
                GlobalScope.launch(Dispatchers.Main) {
                    delay(100)
                    navigateBack()
                }
            }
        }
    }
}