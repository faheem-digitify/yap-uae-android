package co.yap.modules.dashboard.addionalinfo.states

import android.app.Application
import androidx.databinding.ObservableField
import co.yap.modules.dashboard.addionalinfo.interfaces.IAdditionalInfoComplete
import co.yap.yapcore.BaseState

class AdditionalInfoCompleteState(application: Application) : BaseState(),
    IAdditionalInfoComplete.State {
    override val title: ObservableField<String> = ObservableField("")
    override val subTitle: ObservableField<String> = ObservableField("")
}