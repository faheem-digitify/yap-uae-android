package co.yap.modules.auth.main

import co.yap.yapcore.IBase


interface IAuth {
    interface View : IBase.View<ViewModel>
    interface ViewModel : IBase.ViewModel<State> {
        var mobileNo: String
        var countryCode: String
        var passcode: String
    }

    interface State : IBase.State {
        var isAccountBlocked: Boolean
    }
}