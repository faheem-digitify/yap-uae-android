package co.yap.modules.onboarding.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.yap.yapuae.BR
import co.yap.yapuae.R
import co.yap.modules.passcode.IPassCode
import co.yap.modules.passcode.PassCodeViewModel
import co.yap.modules.webview.WebViewFragment
import co.yap.translation.Strings
import co.yap.yapcore.BaseBindingActivity
import co.yap.yapcore.constants.Constants
import co.yap.yapcore.constants.Constants.URL_TERMS_CONDITION
import co.yap.yapcore.constants.RequestCodes
import co.yap.yapcore.databinding.FragmentPassCodeBinding
import co.yap.yapcore.helpers.extentions.startFragment


class CreatePasscodeActivity : BaseBindingActivity<IPassCode.ViewModel>(),
    IPassCode.View {

    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.fragment_pass_code

    override val viewModel: IPassCode.ViewModel
        get() = ViewModelProviders.of(this).get(PassCodeViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.forgotTextVisibility = false
        viewModel.state.title = getString(Strings.screen_create_passcode_display_heading)
        viewModel.state.buttonTitle =
            getString(Strings.screen_create_passcode_onboarding_button_create_passcode)

        getBinding().clTermsAndConditions.visibility = View.VISIBLE
        getBinding().tvTermsAndConditionsTitle.text =
            getString(Strings.screen_confirm_onboarding_create_passcode_display_title_terms_and_conditions)

        getBinding().dialer.hideFingerprintView()
        viewModel.clickEvent.observe(this, Observer {
            when (it) {
                R.id.tvTermsAndConditions -> {
                    startFragment<WebViewFragment>(
                        fragmentName = WebViewFragment::class.java.name, bundle = bundleOf(
                            Constants.PAGE_URL to URL_TERMS_CONDITION
                        ), showToolBar = false
                    )
                }
                R.id.btnAction -> {
                    if (viewModel.isValidPassCode()) {
                        setIntentResults()
                    }
                }
            }
        })
    }

    private fun setIntentResults() {
        val intent = Intent()
        intent.putExtra("PASSCODE", viewModel.state.passCode)
        setResult(RequestCodes.REQUEST_CODE_CREATE_PASSCODE, intent)
        finish()
    }

    override fun onDestroy() {
        viewModel.clickEvent.removeObservers(this)
        super.onDestroy()
    }

    fun getBinding(): FragmentPassCodeBinding {
        return viewDataBinding as FragmentPassCodeBinding
    }

    override fun onToolBarClick(id: Int) {
        when (id) {
            R.id.ivLeftIcon -> {
                super.onBackPressed()
            }
        }
    }
}
