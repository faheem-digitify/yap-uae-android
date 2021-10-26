package co.yap.sendmoney.home.main

import androidx.lifecycle.MutableLiveData
import co.yap.yapcore.BaseState

class SMBeneficiaryParentState : BaseState(), ISMBeneficiaryParent.State {
    override var sendMoneyType: MutableLiveData<String>? = MutableLiveData("")
}
