package co.yap.sendmoney.home.states

import androidx.lifecycle.MutableLiveData
import co.yap.sendmoney.home.interfaces.ISMSearchBeneficiary
import co.yap.widgets.State
import co.yap.yapcore.BaseState

class SMSearchBeneficiaryState : BaseState(), ISMSearchBeneficiary.State {
    override var stateLiveData: MutableLiveData<State>? = MutableLiveData()

}