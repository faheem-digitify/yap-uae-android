package co.yap.modules.location.kyc_additional_info.employment_info.questionnaire.models

import androidx.databinding.ObservableField
import co.yap.modules.location.kyc_additional_info.employment_info.questionnaire.enums.QuestionType

data class Question(
    val questionTitle: String,
    val placeholder: String?,
    val questionType: QuestionType,
    val answer: ObservableField<String>,
    val multipleAnswers: ObservableField<ArrayList<String>> = ObservableField(arrayListOf())
)