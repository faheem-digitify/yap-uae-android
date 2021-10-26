package co.yap.modules.dashboard.addionalinfo.states

import android.app.Application
import androidx.databinding.ObservableField
import co.yap.modules.dashboard.addionalinfo.interfaces.IAdditionalInfoEmployment
import co.yap.yapcore.BaseState

class AdditionalInfoEmploymentState(application: Application) : BaseState(),
    IAdditionalInfoEmployment.State {
    override val title: ObservableField<String> = ObservableField("")
    override val subTitle: ObservableField<String> = ObservableField("")
}