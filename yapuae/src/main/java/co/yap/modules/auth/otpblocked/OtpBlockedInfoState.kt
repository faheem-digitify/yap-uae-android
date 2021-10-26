package co.yap.modules.auth.otpblocked

import androidx.databinding.ObservableField
import co.yap.yapcore.BaseState

class OtpBlockedInfoState : BaseState(), IOtpBlockedInfo.State {
    override val userFirstName: ObservableField<String> = ObservableField()
    override val helpPhoneNo: ObservableField<String> = ObservableField()
    override var token: ObservableField<String> = ObservableField()

}