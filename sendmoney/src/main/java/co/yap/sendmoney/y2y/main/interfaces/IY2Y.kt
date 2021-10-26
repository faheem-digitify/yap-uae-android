package co.yap.sendmoney.y2y.main.interfaces

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import co.yap.networking.customers.requestdtos.Contact
import co.yap.networking.customers.responsedtos.sendmoney.Beneficiary
import co.yap.networking.customers.responsedtos.sendmoney.IBeneficiary
import co.yap.yapcore.IBase

interface IY2Y {

    interface State : IBase.State {
        var tootlBarVisibility: Int
        var rightButtonVisibility: Boolean
        var leftButtonVisibility: Boolean
        var rightIcon: Int
        var fromQR: ObservableBoolean?
    }

    interface ViewModel : IBase.ViewModel<State> {

        val yapContactLiveData: MutableLiveData<List<IBeneficiary>>
        val y2yRecentBeneficiries: MutableLiveData<List<IBeneficiary>>
        val y2yBeneficiries: MutableLiveData<List<IBeneficiary>>
        val isSearching: MutableLiveData<Boolean>
        val selectedTabPos: MutableLiveData<Int>
        val searchQuery: MutableLiveData<String>
        var errorEvent: MutableLiveData<String>
        var beneficiary: Beneficiary?
        var position: Int
        fun getY2YAndY2YRecentBeneficiaries(success: (List<IBeneficiary>) -> Unit)

    }

    interface View : IBase.View<ViewModel>
}