package co.yap.modules.location.kyc_additional_info.employment_info.questionnaire

import androidx.databinding.ObservableField
import co.yap.modules.location.kyc_additional_info.employment_info.questionnaire.models.QuestionUiFields
import co.yap.yapcore.BaseState

class EmploymentQuestionnaireState : BaseState(), IEmploymentQuestionnaire.State {
    override var valid: ObservableField<Boolean> = ObservableField(false)
}