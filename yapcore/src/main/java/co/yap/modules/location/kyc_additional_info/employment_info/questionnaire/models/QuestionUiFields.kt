package co.yap.modules.location.kyc_additional_info.employment_info.questionnaire.models

import androidx.databinding.ObservableBoolean
import co.yap.modules.location.kyc_additional_info.employment_info.questionnaire.enums.QuestionType
import co.yap.yapcore.enums.EmploymentQuestionIdentifier
import co.yap.yapcore.helpers.extentions.parseToDouble

data class QuestionUiFields(
    val key: EmploymentQuestionIdentifier? = null,
    val question: Question,
    val isFocusInput: ObservableBoolean = ObservableBoolean(false)
) {
    fun getAnswer(): String {
        return if (question.questionType == QuestionType.EDIT_TEXT_FIELD_WITH_AMOUNT) question.answer.get()
            .parseToDouble().toString() else question.answer.get().toString()
    }
}