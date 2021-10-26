package co.yap.sendmoney.home.interfaces

import androidx.lifecycle.MutableLiveData
import co.yap.sendmoney.home.adapters.AllBeneficiariesAdapter
import co.yap.yapcore.IBase
import co.yap.yapcore.SingleClickEvent

interface ISMSearchBeneficiary {
    interface View : IBase.View<ViewModel> {
        fun setObservers()
        fun removeObservers()
    }

    interface ViewModel : IBase.ViewModel<State> {
        var clickEvent: SingleClickEvent
        var adapter: AllBeneficiariesAdapter
        fun handlePressOnView(id: Int)
    }

    interface State : IBase.State {
//        var stateLiveData: MutableLiveData<co.yap.widgets.State>
    }
}