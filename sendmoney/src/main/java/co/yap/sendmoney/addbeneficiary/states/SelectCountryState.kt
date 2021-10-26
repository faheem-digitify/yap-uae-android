package co.yap.sendmoney.addbeneficiary.states

import android.app.Application
import androidx.databinding.Bindable
import co.yap.countryutils.country.Country
import co.yap.sendmoney.BR
import co.yap.sendmoney.addbeneficiary.interfaces.ISelectCountry
import co.yap.yapcore.BaseState

class SelectCountryState(val application: Application) : BaseState(), ISelectCountry.State {

    @Bindable
    override var selectedCountry: Country? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.selectedCountry)
            valid = field != null
        }

    @get:Bindable
    override var valid: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.valid)
        }
}