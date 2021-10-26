package co.yap.modules.auth.biometric

import androidx.databinding.Bindable
import co.yap.yapuae.BR
import co.yap.yapcore.BaseState

class SystemPermissionState : BaseState(), ISystemPermission.State {

    @get:Bindable
    override var icon: Int = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.icon)
        }

    override var title: String = ""
        @Bindable get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.title)
        }


    override var subTitle: String = ""
        @Bindable get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.subTitle)
        }

    @get:Bindable
    override var termsAndConditionsVisibility: Boolean = true
        set(value) {
            field = value
            notifyPropertyChanged(BR.termsAndConditionsVisibility)
        }

    @get:Bindable
    override var buttonTitle: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.buttonTitle)
        }
    @get:Bindable
    override var denied: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.denied)
        }

}
