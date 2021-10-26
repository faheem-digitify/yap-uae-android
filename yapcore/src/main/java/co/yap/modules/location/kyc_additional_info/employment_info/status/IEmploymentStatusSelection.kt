package co.yap.modules.location.kyc_additional_info.employment_info.status

import androidx.databinding.ObservableBoolean
import co.yap.yapcore.IBase
import co.yap.yapcore.SingleClickEvent

interface IEmploymentStatusSelection {
    interface View : IBase.View<ViewModel> {
        fun setObservers()
        fun removeObservers()
    }

    interface ViewModel : IBase.ViewModel<State> {
        var clickEvent: SingleClickEvent
        fun handleOnPressNext(id: Int)
        var employmentStatusAdapter: EmploymentStatusAdapter
        var lastItemCheckedPosition: Int
    }

    interface State : IBase.State {
        val enableNextButton: ObservableBoolean
    }
}
