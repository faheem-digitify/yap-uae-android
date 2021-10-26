package co.yap.sendmoney.home.states

import androidx.databinding.ObservableField
import co.yap.sendmoney.home.interfaces.ISMBeneficiaries
import co.yap.yapcore.BaseState

class SendMoneyHomeState : BaseState(), ISMBeneficiaries.State {
    override var isNoBeneficiary: ObservableField<Boolean> = ObservableField(false)
    override var hasBeneficiary: ObservableField<Boolean> = ObservableField(false)
    override var flagDrawableResId: ObservableField<Int> = ObservableField(-1)
    override var isNoRecentBeneficiary: ObservableField<Boolean> = ObservableField(true)
    override var sendMoneyType: ObservableField<String> = ObservableField()
}