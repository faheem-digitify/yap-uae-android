package co.yap.modules.otp

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.databinding.Observable
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.yap.modules.autoreadsms.MySMSBroadcastReceiver
import co.yap.yapcore.BR
import co.yap.yapcore.BaseBindingFragment
import co.yap.yapcore.R
import co.yap.yapcore.constants.Constants
import co.yap.yapcore.databinding.FragmentGenericOtpBinding
import co.yap.yapcore.helpers.Utils
import co.yap.yapcore.helpers.extentions.getOtpFromMessage
import co.yap.yapcore.helpers.extentions.hideKeyboard
import co.yap.yapcore.helpers.extentions.startSmsConsent
import co.yap.yapcore.managers.SessionManager
import com.google.android.gms.auth.api.phone.SmsRetriever
import kotlinx.android.synthetic.main.fragment_generic_otp.*

class GenericOtpFragment : BaseBindingFragment<IGenericOtp.ViewModel>(), IGenericOtp.View {
    private var intentFilter: IntentFilter? = null
    private var appSMSBroadcastReceiver: MySMSBroadcastReceiver? = null
    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.fragment_generic_otp

    override val viewModel: IGenericOtp.ViewModel
        get() = ViewModelProviders.of(this).get(GenericOtpViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { bundle ->
            bundle.getParcelable<OtpDataModel>(OtpDataModel::class.java.name)?.let {
                viewModel.state.otpDataModel = it
            }
        }
        setObservers()
        viewModel.initializeData(requireContext())
    }

    override fun setObservers() {
        viewModel.clickEvent.observe(this, clickEvent)
        viewModel.errorEvent.observe(this, errorEvent)
        SessionManager.onAccountInfoSuccess.observe(this, Observer {
            viewModel.state.loading = false
        })
        viewModel.state.isOtpBlocked.addOnPropertyChangedCallback(stateObserver)
        viewModel.state.otp.addOnPropertyChangedCallback(stateObserverOtp)
        context?.startSmsConsent()
        initBroadcast()
        viewModel.requestKeyBoard.observe(this, Observer {
            if (it) {
                Utils.requestKeyboard(
                    getDataBindingView<FragmentGenericOtpBinding>().otpView,
                    request = true,
                    forced = true
                )
            }
        })
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

    private val stateObserver = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            if (viewModel.state.isOtpBlocked.get() == true) {
                viewModel.state.loading = true
                SessionManager.getAccountInfo()
            }
        }
    }

    private fun initBroadcast() {
        intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        appSMSBroadcastReceiver =
            MySMSBroadcastReceiver(object : MySMSBroadcastReceiver.OnSmsReceiveListener {
                override fun onReceive(code: Intent?) {
                    startActivityForResult(code, Constants.SMS_CONSENT_REQUEST)
                }
            })
    }

    val clickEvent = Observer<Int> {
        when (it) {
            R.id.btnDone -> {
                viewModel.verifyOtp {
                    setResultData()
                }
            }
        }
    }
    val errorEvent = Observer<Int> {
        otpErrorDialog()
    }
    private val stateObserverOtp = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            if (viewModel.isValidOtpLength(viewModel.state.otp.get() ?: "")) {
                viewModel.clickEvent.setValue(R.id.btnDone)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constants.SMS_CONSENT_REQUEST ->
                if (resultCode == Activity.RESULT_OK) {
                    data?.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE).also {
                        viewModel.state.otp.set(it?.getOtpFromMessage() ?: "")
                    }
                }
        }
    }

    override fun setResultData() {
        val intent = Intent()
        viewModel.token.let {
            intent.putExtra("token", viewModel.token)
        }
        intent.putExtra("mobile", viewModel.state.otpDataModel?.mobileNumber)
        activity?.setResult(Activity.RESULT_OK, intent)
        activity?.finish()
    }

    private fun otpErrorDialog() {
        this.activity?.let {
            AlertDialog.Builder(it)
                .setMessage(viewModel.state.errorMessage)
                .setPositiveButton(
                    "Retry"
                ) { _, _ ->
                    viewModel.createOtp(context = requireContext())
                }

                .setNegativeButton(
                    getString(R.string.screen_profile_settings_logout_display_text_alert_cancel)
                ) { _, _ ->
                    activity?.finish()
                }.setCancelable(false)
                .show()
        }
    }

    override fun removeObservers() {
        viewModel.errorEvent.removeObservers(this)
        viewModel.clickEvent.removeObservers(this)
        SessionManager.onAccountInfoSuccess.removeObservers(this)
        viewModel.state.isOtpBlocked.removeOnPropertyChangedCallback(stateObserver)
        viewModel.state.otp.removeOnPropertyChangedCallback(stateObserver)
    }

    override fun onDestroy() {
        super.onDestroy()
        otp_view.hideKeyboard()
        removeObservers()
    }

    override fun onToolBarClick(id: Int) {
        when (id) {
            R.id.ivLeftIcon -> {
                otp_view.hideKeyboard()
                requireActivity().onBackPressed()
            }
        }
    }
}
