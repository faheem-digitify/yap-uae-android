package co.yap.sendmoney.currencyPicker.state

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import co.yap.sendmoney.currencyPicker.interfaces.ICurrencyPicker
import co.yap.widgets.State
import co.yap.yapcore.BaseState

class CurrencyPickerState : BaseState(),
    ICurrencyPicker.State {
    override var currencyDialogChecker: ObservableField<Boolean> = ObservableField()
    override var stateLiveData: MutableLiveData<State>? = MutableLiveData()
}
