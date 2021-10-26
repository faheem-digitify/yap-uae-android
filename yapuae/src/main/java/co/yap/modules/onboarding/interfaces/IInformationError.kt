package co.yap.modules.onboarding.interfaces

import androidx.databinding.ObservableField
import co.yap.yapcore.IBase
import co.yap.yapcore.SingleClickEvent

interface IInformationError {

    interface View : IBase.View<ViewModel>

    interface ViewModel : IBase.ViewModel<State> {
        var clickEvent: SingleClickEvent
        fun handlePressOnGoToDashboard()
        var countryName: String
    }

    interface State : IBase.State {
        var errorTitle: String
        var isUSACitizen: ObservableField<Boolean>
        var errorGuide: String
        var buttonTitle: String
    }
}