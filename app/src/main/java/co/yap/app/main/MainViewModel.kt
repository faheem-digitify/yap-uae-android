package co.yap.app.main

import android.app.Application
import co.yap.modules.onboarding.models.SigningInData
import co.yap.yapcore.BaseViewModel
import co.yap.yapcore.helpers.SharedPreferenceManager

class MainViewModel(application: Application) : BaseViewModel<IMain.State>(application),
    IMain.ViewModel {
    override var signingInData: SigningInData = SigningInData()
    override val state: IMain.State = MainState()
}