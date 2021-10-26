package co.yap.modules.auth.biometric

import co.yap.yapcore.IBase
import co.yap.yapcore.SingleClickEvent
import co.yap.yapcore.SingleLiveEvent

interface ISystemPermission {

    interface View : IBase.View<ViewModel>{
        fun setObservers()
        fun removeObservers()
    }

    interface ViewModel : IBase.ViewModel<State> {
        var screenType: String
        fun handleOnPressView(id: Int)
        val clickEvent: SingleClickEvent
        fun getTouchScreenValues(isGranted : Boolean)
        fun getNotificationScreenValues(isGranted : Boolean)
    }

    interface State : IBase.State {
        var icon: Int
        var title: String
        var subTitle: String
        var termsAndConditionsVisibility: Boolean
        var buttonTitle: String
        var denied: String
    }
}