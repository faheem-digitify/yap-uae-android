package co.yap.sendmoney.addbeneficiary.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import co.yap.countryutils.country.Country
import co.yap.networking.customers.CustomersRepository
import co.yap.networking.interfaces.IRepositoryHolder
import co.yap.networking.models.RetroApiResponse
import co.yap.sendmoney.R
import co.yap.sendmoney.addbeneficiary.interfaces.ISelectCountry
import co.yap.sendmoney.addbeneficiary.states.SelectCountryState
import co.yap.sendmoney.viewmodels.SendMoneyBaseViewModel
import co.yap.translation.Strings
import co.yap.yapcore.SingleClickEvent
import co.yap.yapcore.enums.SendMoneyBeneficiaryType
import co.yap.yapcore.enums.SendMoneyTransferType
import co.yap.yapcore.helpers.Utils
import co.yap.yapcore.managers.SessionManager

class SelectCountryViewModel(application: Application) :
    SendMoneyBaseViewModel<ISelectCountry.State>(application), ISelectCountry.ViewModel,
    IRepositoryHolder<CustomersRepository> {

    override var populateSpinnerData: MutableLiveData<List<Country>> = MutableLiveData()
    override val repository: CustomersRepository = CustomersRepository
    override val state: SelectCountryState = SelectCountryState(application)
    override var clickEvent: SingleClickEvent = SingleClickEvent()

    override fun onTransparentViewClick(id: Int) {
        clickEvent.setValue(id)
    }

    override fun handlePressOnSeclectCountry(id: Int) {
        if (id == R.id.nextButton) {
            parentViewModel?.selectedCountry?.value = state.selectedCountry
            parentViewModel?.selectedCountry?.value?.let { country ->
                parentViewModel?.beneficiary?.value?.beneficiaryType =
                    getBeneficiaryTypeFromCurrency(country)
                clickEvent.setValue(id)
            }
        } else {
            clickEvent.setValue(id)
        }
    }

    override fun onCreate() {
        super.onCreate()
        getAllCountries()
    }

    override fun onResume() {
        super.onResume()
        state.valid = false
        setToolBarTitle(getString(Strings.screen_add_beneficiary_display_text_title))
        parentViewModel?.state?.toolbarVisibility?.set(true)
        parentViewModel?.state?.leftIconVisibility?.set(true)

    }

    override fun getBeneficiaryTypeFromCurrency(country: Country?): String? {
        country?.let {
            if (country.isoCountryCode2Digit == "AE") return SendMoneyBeneficiaryType.DOMESTIC.name
            return country.getCurrency()?.cashPickUp?.let { it ->
                if (!it) {
                    country.getCurrency()?.rmtCountry?.let { isRmt ->
                        if (isRmt) {
                            SendMoneyBeneficiaryType.RMT.name
                        } else {
                            SendMoneyBeneficiaryType.SWIFT.name
                        }
                    }
                } else {
                    SendMoneyBeneficiaryType.CASHPAYOUT.name
                }
            }
        } ?: return ""
    }

    private fun getAllCountries() {
        if (!populateSpinnerData.value.isNullOrEmpty()) {
            populateSpinnerData.setValue(parentViewModel?.countriesList)
        } else {
            launch {
                state.loading = true
                when (val response = repository.getCountries()) {
                    is RetroApiResponse.Success -> {
                        populateSpinnerData.value =
                            Utils.parseCountryList(response.data.data, false)
                        parentViewModel?.countriesList = populateSpinnerData.value
                        state.loading = false
                    }

                    is RetroApiResponse.Error -> {
                        state.loading = false
                        state.toast = response.error.message
                    }
                }
            }
        }
    }

    private fun getExcludedCountryIsoCode(): String {
        return when (parentViewModel?.sendMoneyType) {
            SendMoneyTransferType.HOME_COUNTRY.name -> SessionManager.user?.currentCustomer?.homeCountry
                ?: ""
            SendMoneyTransferType.LOCAL.name -> "AE"

            else -> {
                ""
            }
        }
    }

    override fun onCountrySelected(country: Country?) {
        state.selectedCountry =
            parentViewModel?.countriesList?.find { it.isoCountryCode2Digit == country?.isoCountryCode2Digit }
        parentViewModel?.selectedResidenceCountry = null
    }
}