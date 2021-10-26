package co.yap.modules.dashboard.yapit.sendmoney.homecountry

import android.app.Application
import co.yap.yapuae.R
import co.yap.countryutils.country.Country
import co.yap.networking.customers.CustomersRepository
import co.yap.networking.customers.responsedtos.sendmoney.Beneficiary
import co.yap.networking.customers.responsedtos.sendmoney.FxRateRequest
import co.yap.networking.customers.responsedtos.sendmoney.GetAllBeneficiaryResponse
import co.yap.networking.interfaces.IRepositoryHolder
import co.yap.networking.models.ApiResponse
import co.yap.networking.models.RetroApiResponse
import co.yap.networking.transactions.responsedtos.transaction.FxRateResponse
import co.yap.widgets.recent_transfers.CoreRecentTransferAdapter
import co.yap.yapcore.BaseViewModel
import co.yap.yapcore.Dispatcher
import co.yap.yapcore.SingleClickEvent
import co.yap.yapcore.helpers.DateUtils
import co.yap.yapcore.managers.SessionManager

class SMHomeCountryViewModel(application: Application) :
    BaseViewModel<ISMHomeCountry.State>(application), ISMHomeCountry.ViewModel,
    IRepositoryHolder<CustomersRepository> {
    override val clickEvent: SingleClickEvent = SingleClickEvent()
    override val repository: CustomersRepository = CustomersRepository
    override val state: SMHomeCountryState = SMHomeCountryState()
    override var homeCountry: Country? = null
    override var benefitsList: ArrayList<String> = ArrayList()
    override var recentsAdapter: CoreRecentTransferAdapter = CoreRecentTransferAdapter(
        context,
        mutableListOf()
    )
    override var benefitsAdapter: SMHomeCountryBenefitsAdapter =
        SMHomeCountryBenefitsAdapter(benefitsList)

    override fun handlePressOnView(id: Int) {
        clickEvent.setValue(id)
    }

    override fun onCreate() {
        super.onCreate()
        state.toolbarTitle = getString(R.string.screen_send_money_home_title)
        state.rightButtonText.set(getString(R.string.screen_send_money_home_display_text_compare))

        homeCountry = if (SessionManager.user?.currentCustomer?.homeCountry?.count() ?: 0 > 2)
            SessionManager.getCountries()
                .find { it.isoCountryCode3Digit == SessionManager.user?.currentCustomer?.homeCountry ?: "" }
        else
            SessionManager.getCountries()
                .find { it.isoCountryCode2Digit == SessionManager.user?.currentCustomer?.homeCountry ?: "" }

        homeCountry?.let { populateData(it) }
        benefitsList.add(getString(R.string.screen_send_money_home_display_text_send_money_home))
        benefitsList.add(getString(R.string.screen_send_money_home_display_text_get_best_rates))
        getFxRates(iso2DigitCountryCode = homeCountry?.isoCountryCode2Digit ?: "") { response ->
            handleFxRateResponse(
                response
            )
        }
    }

    override fun populateData(hc: Country) {
        state.name?.set(hc.getName())
        state.countryCode?.set(hc.isoCountryCode2Digit)
    }

    override fun getHomeCountryRecentBeneficiaries() {
        launch(Dispatcher.Background) {
            state.viewState.postValue(true)
            val response = repository.getRecentBeneficiaries()
            launch {
                when (response) {
                    is RetroApiResponse.Success -> {
                        handleRecentsList(response.data.data)
                        state.viewState.value = false
                    }
                    is RetroApiResponse.Error -> {
                        state.viewState.value = false
                        state.viewState.value = response.error.message
                    }
                }
            }
        }
    }

    override fun updateAndSyncHomeCountry() {
        updateAlApis { updateCountryResponse, fxRateResponse, recentsBeneficiaries ->
            launch(Dispatcher.Main) {
                when (updateCountryResponse) {
                    is RetroApiResponse.Success -> {
                        SessionManager.getAccountInfo()
                        homeCountry?.let { populateData(it) }
                    }
                }
                when (fxRateResponse) {
                    is RetroApiResponse.Success -> {
                        state.showExchangeRate.set(true)
                        handleFxRateResponse(fxRateResponse.data.data)
                    }
                    is RetroApiResponse.Error -> {
                        state.viewState.value = fxRateResponse.error.message
                        state.showExchangeRate.set(false)
                    }
                }
                when (recentsBeneficiaries) {
                    is RetroApiResponse.Success -> {
                        handleRecentsList(recentsBeneficiaries.data.data)
                    }
                    is RetroApiResponse.Error -> {
                        state.viewState.value = recentsBeneficiaries.error.message
                    }
                }
                state.viewState.value = false
            }
        }
    }

    private fun updateAlApis(
        responses: (RetroApiResponse<ApiResponse>?, RetroApiResponse<FxRateResponse>?, RetroApiResponse<GetAllBeneficiaryResponse>) -> Unit
    ) {
        launch(Dispatcher.Background) {
            state.viewState.postValue(true)
            val deferredUpdateCountryResponse = launchAsync {
                repository.updateHomeCountry(
                    homeCountry = homeCountry?.isoCountryCode2Digit ?: ""
                )
            }
            val response = deferredUpdateCountryResponse.await()
            when (response) {
                is RetroApiResponse.Success -> {
                    val deferredExchangeRateResponse = launchAsync {
                        repository.updateFxRate(FxRateRequest(other_bank_country = homeCountry?.isoCountryCode2Digit.toString()))
                    }
                    val deferredRecents = launchAsync { repository.getRecentBeneficiaries() }
                    responses(
                        response,
                        deferredExchangeRateResponse.await(),
                        deferredRecents.await()
                    )
                }
                is RetroApiResponse.Error -> {
                    state.viewState.postValue(false)
                }
            }
        }
    }

    override fun getFxRates(iso2DigitCountryCode: String, fxRate: (FxRateResponse.Data) -> Unit) {
        launch(Dispatcher.Background) {
            val response =
                repository.updateFxRate(FxRateRequest(other_bank_country = iso2DigitCountryCode))
            launch {
                when (response) {
                    is RetroApiResponse.Success -> {
                        state.showExchangeRate.set(true)
                        fxRate.invoke(response.data.data)
                    }

                    is RetroApiResponse.Error -> {
                        state.showExchangeRate.set(false)
                        state.viewState.value = response.error.message
                    }
                }
            }
        }
    }

    override fun handleFxRateResponse(it: FxRateResponse.Data?) {
        it?.let { fxRate ->
            state.rate?.set("${fxRate.fxRates?.get(0)?.rate}")
            state.homeCountryCurrency?.set(fxRate.toCurrencyCode)
            state.time?.set(
                DateUtils.reformatStringDate(
                    fxRate.date.toString(),
                    inputFormatter = DateUtils.SERVER_DATE_FORMAT,
                    outFormatter = DateUtils.FXRATE_DATE_TIME_FORMAT
                )
            )
        }
    }

    private fun handleRecentsList(recentsList: List<Beneficiary>) {
        recentsList.forEach {
            it.name = it.fullName()
            it.profilePictureUrl = it.beneficiaryPictureUrl
            it.type = it.beneficiaryType
            it.isoCountryCode = it.country
        }
        val homeCountryList: List<Beneficiary> =
            recentsList.filter { it.country == homeCountry?.isoCountryCode2Digit }
        recentsAdapter.setList(homeCountryList)
        state.isNoRecentsBeneficiries.set(
            recentsAdapter.getDataList().isNullOrEmpty()
        )
    }
}