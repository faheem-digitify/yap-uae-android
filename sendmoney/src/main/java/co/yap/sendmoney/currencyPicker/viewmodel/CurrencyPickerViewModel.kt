package co.yap.sendmoney.currencyPicker.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import co.yap.sendmoney.currencyPicker.interfaces.ICurrencyPicker
import co.yap.sendmoney.currencyPicker.model.MultiCurrencyWallet
import co.yap.sendmoney.currencyPicker.adapters.CurrencyAdapter
import co.yap.sendmoney.currencyPicker.state.CurrencyPickerState
import co.yap.yapcore.SingleClickEvent

class CurrencyPickerViewModel(application: Application) :
    MCLandingBaseViewModel<ICurrencyPicker.State>(application),
    ICurrencyPicker.ViewModel {

    override val state: ICurrencyPicker.State =
        CurrencyPickerState()

    override val clickEvent: SingleClickEvent = SingleClickEvent()

    override fun handlePressOnView(id: Int) {
        clickEvent.setValue(id)
    }

    override val currencyAdapter: CurrencyAdapter =
        CurrencyAdapter(mutableListOf())

    override var availableCurrenciesList: MutableList<MultiCurrencyWallet> = mutableListOf()

    override var searchQuery: MutableLiveData<String> = MutableLiveData()

    override fun onCreate() {
        super.onCreate()
        currencyAdapter.setList(availableCurrenciesList)
    }
}
