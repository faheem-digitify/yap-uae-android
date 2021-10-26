package co.yap.modules.dashboard.addionalinfo.viewmodels

import android.view.View
import co.yap.modules.dashboard.addionalinfo.model.AdditionalInfoEmployment
import co.yap.yapcore.interfaces.OnItemClickListener

class AdditionalInfoEmploymentItemViewModel(
    var additionalInfoEmployment: AdditionalInfoEmployment,
    var position: Int,
    private val onItemClickListener: OnItemClickListener?
) {
    fun handlePressOnView(view: View) {
        onItemClickListener?.onItemClick(view, additionalInfoEmployment, position)
    }
}