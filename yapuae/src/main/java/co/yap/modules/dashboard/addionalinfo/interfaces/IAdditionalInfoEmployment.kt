package co.yap.modules.dashboard.addionalinfo.interfaces

import androidx.databinding.ObservableField
import co.yap.modules.dashboard.addionalinfo.adapters.AdditionalInfoEmploymentAdapter
import co.yap.yapcore.IBase

interface IAdditionalInfoEmployment {
    interface View : IBase.View<ViewModel>

    interface ViewModel : IBase.ViewModel<State> {
        val additionalInfoEmploymentAdapter: AdditionalInfoEmploymentAdapter
        fun moveToNext()
    }

    interface State : IBase.State {
        val title: ObservableField<String>
        val subTitle: ObservableField<String>
    }
}