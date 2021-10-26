package co.yap.sendmoney.addbeneficiary.fragments

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import co.yap.countryutils.country.Country
import co.yap.countryutils.country.utils.Currency
import co.yap.sendmoney.BR
import co.yap.sendmoney.R
import co.yap.sendmoney.addbeneficiary.interfaces.ISelectCountry
import co.yap.sendmoney.addbeneficiary.viewmodels.SelectCountryViewModel
import co.yap.sendmoney.fragments.SendMoneyBaseFragment
import co.yap.yapcore.constants.Constants
import co.yap.yapcore.enums.SendMoneyBeneficiaryType
import co.yap.yapcore.enums.SendMoneyTransferType
import co.yap.yapcore.firebase.FirebaseEvent
import co.yap.yapcore.firebase.trackEventWithScreenName
import co.yap.yapcore.helpers.extentions.launchBottomSheet
import co.yap.yapcore.interfaces.OnItemClickListener
import co.yap.yapcore.managers.SessionManager

class SelectCountryFragment : SendMoneyBaseFragment<ISelectCountry.ViewModel>(),
    ISelectCountry.View {

    override fun getBindingVariable(): Int = BR.viewModel
    override fun getLayoutId(): Int = R.layout.fragment_select_country

    override val viewModel: SelectCountryViewModel
        get() = ViewModelProviders.of(this).get(SelectCountryViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        when (viewModel.parentViewModel?.sendMoneyType) {
            SendMoneyTransferType.LOCAL.name -> skipToAddDomestic()
            SendMoneyTransferType.HOME_COUNTRY.name -> skipToAddBeneficiary()
        }
    }

    private fun skipToAddBeneficiary() {
        val homeCountry = SessionManager.getCountries()
            .find { it.isoCountryCode2Digit == SessionManager.homeCountry2Digit }
        viewModel.parentViewModel?.beneficiary?.value?.beneficiaryType =
            viewModel.getBeneficiaryTypeFromCurrency(homeCountry)
        viewModel.parentViewModel?.selectedCountry?.value = homeCountry
        viewModel.parentViewModel?.countriesList = SessionManager.getCountries()
        skipCountrySelectionFragment(R.id.action_selectCountryFragment_to_addBeneficiaryFragment)
    }

    private fun skipToAddDomestic() {
        viewModel.parentViewModel?.selectedCountry?.value = Country(
            isoCountryCode2Digit = "AE",
            name = "United Arab Emirates",
            ibanMandatory = true,
            currency = Currency(code = "AED")
        )
        viewModel.parentViewModel?.beneficiary?.value?.beneficiaryType =
            SendMoneyBeneficiaryType.DOMESTIC.name
        skipCountrySelectionFragment(R.id.action_selectCountryFragment_to_DomesticFragment)
    }

    override fun onPause() {
        viewModel.clickEvent.removeObservers(this)
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        viewModel.clickEvent.observe(this, Observer {
            when (it) {
                R.id.nextButton -> {
                    if (!isDefaultCurrencyExist()) {
                        trackEventWithScreenName(
                            FirebaseEvent.START_NEW_BENEFICIARY,
                            bundleOf("beneficiary_country" to viewModel.state.selectedCountry?.getName())
                        )
                        viewModel.state.selectedCountry?.getCurrency()?.let { it ->
                            it.cashPickUp?.let { cashPickup ->
                                if (cashPickup) {
                                    moveToTransferType()
                                } else {
                                    moveToAddBeneficiary()
                                }
                            }
                        }
                    } else {
                        showToast("No active currencies found for selected country")
                    }
                }

                R.id.tvCountrySelect -> {
                    this.launchBottomSheet(
                        itemClickListener = itemListener,
                        label = "Select Country",
                        viewType = Constants.VIEW_WITH_FLAG,
                        countriesList = viewModel.populateSpinnerData.value?.filter { country -> country.isoCountryCode2Digit != "AE" }
                    )
                }
            }
        })
    }

    private val itemListener = object : OnItemClickListener {
        override fun onItemClick(view: View, data: Any, pos: Int) {
            viewModel.onCountrySelected(data as Country)
        }
    }


    private fun isDefaultCurrencyExist(): Boolean {
        return null == viewModel.state.selectedCountry?.getCurrencySM()
    }

    private fun moveToAddBeneficiary() {
        findNavController().navigate(R.id.action_selectCountryFragment_to_addBeneficiaryFragment)
    }

    private fun moveToTransferType() {
        findNavController().navigate(R.id.action_selectCountryFragment_to_transferTypeFragment)
    }

    private fun skipCountrySelectionFragment(destinationId: Int) {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.selectCountryFragment, true) // starting destination skiped
            .build()

        findNavController().navigate(
            destinationId,
            null,
            navOptions
        )
    }
}