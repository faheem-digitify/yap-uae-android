package co.yap.sendmoney.home.interfaces

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import co.yap.networking.customers.requestdtos.Contact
import co.yap.networking.customers.responsedtos.sendmoney.Beneficiary
import co.yap.sendmoney.home.adapters.AllBeneficiariesAdapter
import co.yap.widgets.recent_transfers.CoreRecentTransferAdapter
import co.yap.yapcore.IBase
import co.yap.yapcore.SingleClickEvent
import co.yap.yapcore.helpers.PagingState

interface ISMBeneficiaries {
    interface State : IBase.State {
        var isNoBeneficiary: ObservableField<Boolean>
        var hasBeneficiary: ObservableField<Boolean>
        var isNoRecentBeneficiary: ObservableField<Boolean>
        var flagDrawableResId: ObservableField<Int>
        var sendMoneyType: ObservableField<String>
    }

    interface ViewModel : IBase.ViewModel<State> {
        var clickEvent: SingleClickEvent
        val recentTransferData: MutableLiveData<List<Beneficiary>>
        var recentsAdapter: CoreRecentTransferAdapter
        var beneficiariesAdapter: AllBeneficiariesAdapter
        fun handlePressOnView(id: Int)
        fun requestRecentBeneficiaries(sendMoneyType: String)
    }

    interface View : IBase.View<ViewModel>
}