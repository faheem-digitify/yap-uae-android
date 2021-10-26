package co.yap.modules.dashboard.yapit.addmoney.landing

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.yap.yapuae.BR
import co.yap.yapuae.R
import co.yap.yapuae.databinding.FragmentAddMoneyLandingBinding
import co.yap.modules.dashboard.more.cdm.CdmMapFragment
import co.yap.modules.dashboard.yapit.addmoney.main.AddMoneyBaseFragment
import co.yap.modules.dashboard.yapit.topup.cardslisting.TopUpBeneficiariesActivity
import co.yap.modules.dashboard.yapit.topup.topupbankdetails.TopUpBankDetailsFragment
import co.yap.translation.Strings
import co.yap.widgets.SpaceGridItemDecoration
import co.yap.widgets.qrcode.QRCodeFragment
import co.yap.yapcore.constants.Constants
import co.yap.yapcore.constants.Constants.SUCCESS_BUTTON_LABEL
import co.yap.yapcore.constants.RequestCodes
import co.yap.yapcore.firebase.FirebaseEvent
import co.yap.yapcore.firebase.trackEventWithScreenName
import co.yap.yapcore.helpers.extentions.dimen
import co.yap.yapcore.helpers.extentions.launchActivity
import co.yap.yapcore.helpers.extentions.startFragment
import co.yap.yapcore.interfaces.OnItemClickListener

class AddMoneyLandingFragment : AddMoneyBaseFragment<IAddMoneyLanding.ViewModel>(),
    IAddMoneyLanding.View {
    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.fragment_add_money_landing

    override val viewModel: IAddMoneyLanding.ViewModel
        get() = ViewModelProviders.of(this).get(AddMoneyLandingViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setObservers()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycleView()
    }

    override fun setObservers() {
        viewModel.clickEvent.observe(this, observer)
    }

    override fun removeObservers() {
        viewModel.clickEvent.removeObservers(this)
    }

    private fun setupRecycleView() {
        getBinding().recyclerOptions.addItemDecoration(
            SpaceGridItemDecoration(
                dimen(R.dimen.margin_normal_large), 2, true
            )
        )
        viewModel.landingAdapter.allowFullItemClickListener = true
        viewModel.landingAdapter.setItemListener(listener)
    }

    private val listener = object : OnItemClickListener {
        override fun onItemClick(view: View, data: Any, pos: Int) {
            if (data is AddMoneyLandingOptions)
                viewModel.clickEvent.setValue(data.id)
        }
    }


    private val observer = Observer<Int> {
        when (it) {
            Constants.ADD_MONEY_TOP_UP_VIA_CARD -> {
                trackEventWithScreenName(FirebaseEvent.CLICK_TOPUP_CARD)
                launchActivity<TopUpBeneficiariesActivity>(requestCode = RequestCodes.REQUEST_SHOW_BENEFICIARY) {
                    putExtra(
                        SUCCESS_BUTTON_LABEL,
                        getString(Strings.screen_topup_success_display_text_dashboard_action_button_title)
                    )
                }
            }
            Constants.ADD_MONEY_SAMSUNG_PAY -> {
                showToast(getString(Strings.screen_fragment_yap_it_add_money_text_samsung_pay))
            }
            Constants.ADD_MONEY_GOOGLE_PAY -> {
                showToast(getString(Strings.screen_fragment_yap_it_add_money_text_google_pay))
            }
            Constants.ADD_MONEY_BANK_TRANSFER -> {
                trackEventWithScreenName(FirebaseEvent.CLICK_TOPUP_TRANSFER)
                startFragment(
                    TopUpBankDetailsFragment::class.java.name,
                    false,
                    bundleOf(
                    )
                )
            }
            Constants.ADD_MONEY_CASH_OR_CHEQUE -> {
                startFragment(
                    fragmentName = CdmMapFragment::class.java.name, bundle = bundleOf(
                        Constants.LOCATION_TYPE to Constants.LOCATION_CDM
                    )
                )
            }
            Constants.ADD_MONEY_QR_CODE -> {
                QRCodeFragment {}.let { fragment ->
                    trackEventWithScreenName(FirebaseEvent.TOPUP_QR_CODE)
                    if (isAdded)
                        fragment.show(requireActivity().supportFragmentManager, "")
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RequestCodes.REQUEST_SHOW_BENEFICIARY) {
                if (RequestCodes.REQUEST_SHOW_BENEFICIARY == data?.getIntExtra(
                        RequestCodes.REQUEST_SHOW_BENEFICIARY.toString(),
                        0
                    )
                ) {
                    requireActivity().finish()
                }
            }
        }
    }

    override fun getBinding(): FragmentAddMoneyLandingBinding {
        return viewDataBinding as FragmentAddMoneyLandingBinding
    }
}
