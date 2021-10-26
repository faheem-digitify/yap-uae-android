package co.yap.modules.location.kyc_additional_info.employment_info.status

import android.view.View
import co.yap.yapcore.interfaces.OnItemClickListener

class EmploymentStatusSelectionItemViewModel(
    val employmentStatusSelectionModel: EmploymentStatusSelectionModel,
    val position: Int,
    private val onItemClickListener: OnItemClickListener?
) {
    fun handlePressOnView(view: View) {
        onItemClickListener?.onItemClick(view, employmentStatusSelectionModel, position)
    }
}
