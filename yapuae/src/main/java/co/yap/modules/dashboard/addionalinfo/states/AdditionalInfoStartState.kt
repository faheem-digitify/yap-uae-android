package co.yap.modules.dashboard.addionalinfo.states

import android.app.Application
import androidx.databinding.ObservableField
import co.yap.modules.dashboard.addionalinfo.interfaces.IAdditionalInfoStart
import co.yap.yapcore.BaseState

class AdditionalInfoStartState(application: Application) : BaseState(),
    IAdditionalInfoStart.State {
    override val title: ObservableField<String> = ObservableField("")
    override val subTitle: ObservableField<String> = ObservableField("")
}