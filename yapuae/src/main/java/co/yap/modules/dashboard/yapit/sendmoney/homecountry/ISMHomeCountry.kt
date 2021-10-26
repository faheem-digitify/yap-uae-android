package co.yap.modules.dashboard.yapit.sendmoney.homecountry

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import co.yap.countryutils.country.Country
import co.yap.networking.transactions.responsedtos.transaction.FxRateResponse
import co.yap.widgets.recent_transfers.CoreRecentTransferAdapter
import co.yap.yapcore.IBase
import co.yap.yapcore.SingleClickEvent

interface ISMHomeCountry {
    interface State : IBase.State {
        var countryCode: ObservableField<String>?
        var name: ObservableField<String>?
        var rate: ObservableField<String>?
        var homeCountryCurrency: ObservableField<String>?
        var time: ObservableField<String>?
        var rightButtonText: ObservableField<String>
        var isNoRecentsBeneficiries: ObservableBoolean
        var isRecentsVisible: ObservableBoolean
        var showExchangeRate: ObservableBoolean
    }

    interface ViewModel : IBase.ViewModel<State> {
        val clickEvent: SingleClickEvent
        var recentsAdapter: CoreRecentTransferAdapter
        var benefitsAdapter: SMHomeCountryBenefitsAdapter
        var homeCountry: Country?
        var benefitsList: ArrayList<String>
        fun populateData(hc: Country)
        fun getHomeCountryRecentBeneficiaries()
        fun handlePressOnView(id: Int)
        fun getFxRates(iso2DigitCountryCode: String, fxRate: (FxRateResponse.Data) -> Unit)
        fun handleFxRateResponse(it: FxRateResponse.Data?)
        fun updateAndSyncHomeCountry()
    }

    interface View : IBase.View<ViewModel> {

    }
}