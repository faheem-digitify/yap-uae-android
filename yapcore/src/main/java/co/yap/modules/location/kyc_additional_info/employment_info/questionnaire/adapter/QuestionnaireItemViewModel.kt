package co.yap.modules.location.kyc_additional_info.employment_info.questionnaire.adapter

import android.view.View
import co.yap.modules.location.kyc_additional_info.employment_info.questionnaire.models.QuestionUiFields
import co.yap.yapcore.interfaces.OnItemClickListener

class QuestionnaireItemViewModel(
    val questionUiFields: QuestionUiFields,
    val position: Int,
    val onItemClickListener: OnItemClickListener?
) {

    fun onViewClicked(view: View) {
        onItemClickListener?.onItemClick(view, questionUiFields, position)
    }
}