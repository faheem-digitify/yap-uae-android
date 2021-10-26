package co.yap.sendmoney.states
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import co.yap.sendmoney.interfaces.ISendMoney
import co.yap.yapcore.BaseState

class SMBeneficiariesState : BaseState(), ISendMoney.State {

    override var rightIconVisibility: ObservableBoolean = ObservableBoolean(false)
    override var leftIconVisibility: ObservableBoolean = ObservableBoolean(false)
    override var toolbarVisibility: ObservableBoolean = ObservableBoolean(false)
    override var rightButtonText: ObservableField<String> = ObservableField("Cancel")
}