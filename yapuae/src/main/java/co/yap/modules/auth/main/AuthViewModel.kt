package co.yap.modules.auth.main

import android.app.Application
import co.yap.yapcore.BaseViewModel

class AuthViewModel(application: Application) : BaseViewModel<IAuth.State>(application),
    IAuth.ViewModel {
    override var mobileNo: String = ""
    override var countryCode: String = ""
    override var passcode: String = ""
    override val state: IAuth.State = AuthState()
}