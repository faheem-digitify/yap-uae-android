package co.yap.sendmoney.home.main

import androidx.lifecycle.MutableLiveData
import co.yap.networking.customers.requestdtos.Contact
import co.yap.networking.customers.responsedtos.sendmoney.Beneficiary
import co.yap.networking.customers.responsedtos.sendmoney.IBeneficiary
import co.yap.yapcore.IBase

interface ISMBeneficiaryParent {
    interface View : IBase.View<ViewModel>
    interface ViewModel : IBase.ViewModel<State> {
        var beneficiariesList: MutableLiveData<ArrayList<IBeneficiary>>
        fun getBeneficiaryFromContact(beneficiary: IBeneficiary): Beneficiary
        fun requestDeleteBeneficiary(beneficiaryId: String, completion: () -> Unit)
        fun requestAllBeneficiaries(sendMoneyType: String, completion: () -> Unit={})
        fun getBeneficiariesOfType(type: String, list: List<Beneficiary>): List<Beneficiary>
    }

    interface State : IBase.State {
        var sendMoneyType: MutableLiveData<String>?
    }
}
