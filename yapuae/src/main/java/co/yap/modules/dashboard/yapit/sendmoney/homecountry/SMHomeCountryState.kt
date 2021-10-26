package co.yap.modules.dashboard.yapit.sendmoney.homecountry

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import co.yap.yapcore.BaseState

class SMHomeCountryState : BaseState(), ISMHomeCountry.State {

    override var countryCode: ObservableField<String>? = ObservableField()
    override var name: ObservableField<String>? = ObservableField()
    override var rate: ObservableField<String>? = ObservableField()
    override var homeCountryCurrency: ObservableField<String>? = ObservableField()
    override var time: ObservableField<String>? = ObservableField()
    override var rightButtonText: ObservableField<String> = ObservableField("")
    override var isNoRecentsBeneficiries: ObservableBoolean = ObservableBoolean(true)
    override var isRecentsVisible: ObservableBoolean = ObservableBoolean(false)
    override var showExchangeRate: ObservableBoolean = ObservableBoolean(false)

}