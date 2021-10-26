package co.yap.modules.location.kyc_additional_info.employment_info.status

import androidx.databinding.ObservableBoolean
import co.yap.yapcore.BaseState

class EmploymentStatusSelectionState : BaseState(),
    IEmploymentStatusSelection.State {
    override val enableNextButton: ObservableBoolean = ObservableBoolean(false)
}
