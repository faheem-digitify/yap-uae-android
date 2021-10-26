package co.yap.modules.onboarding.states

import androidx.databinding.Bindable
import co.yap.yapuae.BR
import co.yap.modules.onboarding.interfaces.IOnboarding
import co.yap.yapcore.BaseState

class OnboardingState : BaseState(), IOnboarding.State {

    @get:Bindable
    override var totalProgress: Int = 100
        set(value) {
            field = value
            notifyPropertyChanged(BR.totalProgress)
        }

    @get:Bindable
    override var currentProgress: Int = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.currentProgress)
        }

    @get:Bindable
    override var emailError: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.emailError)
        }
}