package co.yap.sendmoney.y2y.home.yapcontacts

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import co.yap.widgets.State
import co.yap.yapcore.BaseState

class YapContactState : BaseState(), IYapContact.State {
    override var stateLiveData: MutableLiveData<State>? = MutableLiveData(State.loading(null))
    override var isNoYapContacts: ObservableBoolean = ObservableBoolean(false)
    override var isNoSearchResult: ObservableBoolean = ObservableBoolean(false)
    override var isShowContactsCounter: ObservableBoolean = ObservableBoolean(false)
    override var contactsCounts: ObservableInt = ObservableInt()
}