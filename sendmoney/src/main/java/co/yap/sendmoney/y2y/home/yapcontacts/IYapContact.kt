package co.yap.sendmoney.y2y.home.yapcontacts

import android.os.Bundle
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import co.yap.networking.customers.responsedtos.sendmoney.IBeneficiary
import co.yap.yapcore.IBase
import co.yap.yapcore.SingleClickEvent

interface IYapContact {

    interface View : IBase.View<ViewModel> {
    }

    interface ViewModel : IBase.ViewModel<State> {
        val clickEvent: SingleClickEvent
        var contactsAdapter: YapContactsAdaptor
        fun handlePressOnView(id: Int)
        fun getBundle(data: IBeneficiary, pos: Int): Bundle
        fun getActionId(fragment: Fragment?): Int
    }

    interface State : IBase.State {
        var isNoYapContacts: ObservableBoolean
        var isNoSearchResult: ObservableBoolean
        var isShowContactsCounter: ObservableBoolean
        var contactsCounts: ObservableInt
    }
}