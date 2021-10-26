package co.yap.modules.location.kyc_additional_info.employment_info.questionnaire.adapter

import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.databinding.ViewDataBinding
import co.yap.modules.location.kyc_additional_info.employment_info.questionnaire.EmploymentQuestionnaireViewModel
import co.yap.modules.location.kyc_additional_info.employment_info.questionnaire.enums.QuestionType
import co.yap.modules.location.kyc_additional_info.employment_info.questionnaire.models.QuestionUiFields
import co.yap.yapcore.R
import co.yap.yapcore.databinding.LayoutQuestionTypeCountriesBinding
import co.yap.yapcore.databinding.LayoutQuestionTypeDropDownBinding
import co.yap.yapcore.databinding.LayoutQuestionTypeEditTextBinding
import co.yap.yapcore.databinding.LayoutQuestionTypeEditTextWithAmountBinding
import co.yap.yapcore.helpers.extentions.afterTextChanged
import co.yap.yapcore.helpers.extentions.hideKeyboard
import co.yap.yapcore.interfaces.OnItemClickListener

class QuestionItemViewHolders constructor(private val viewModel: EmploymentQuestionnaireViewModel) {
    private fun getLayoutId(forType: QuestionType): Int {
        return when (forType) {
            QuestionType.EDIT_TEXT_FIELD -> R.layout.layout_question_type_edit_text
            QuestionType.EDIT_TEXT_FIELD_WITH_AMOUNT -> R.layout.layout_question_type_edit_text_with_amount
            QuestionType.COUNTRIES_FIELD -> R.layout.layout_question_type_countries
            QuestionType.DROP_DOWN_FIELD -> R.layout.layout_question_type_drop_down
        }
    }

    fun questionTypeEditTextItemViewHolder(
        binding: LayoutQuestionTypeEditTextBinding,
        questionUiFields: QuestionUiFields,
        position: Int,
        onItemClickListener: OnItemClickListener?
    ): View {
        binding.viewModel =
            QuestionnaireItemViewModel(
                questionUiFields,
                position,
                onItemClickListener
            )
        binding.etQuestionEditText.afterTextChanged {
            onItemClickListener?.onItemClick(
                binding.etQuestionEditText,
                it,
                -1
            )
        }
        setFocusListener(binding.etQuestionEditText, questionUiFields)
        return binding.root
    }

    fun questionTypeEditTextWithAmountItemViewHolder(
        binding: LayoutQuestionTypeEditTextWithAmountBinding,
        questionUiFields: QuestionUiFields,
        position: Int,
        onItemClickListener: OnItemClickListener?
    ): View {
        binding.viewModel =
            QuestionnaireItemViewModel(
                questionUiFields,
                position,
                onItemClickListener
            )
        binding.ivSupport.setOnClickListener {
            binding.etAmount.hideKeyboard()
            onItemClickListener?.onItemClick(binding.ivSupport, questionUiFields, -1)
        }
        binding.etAmount.afterTextChanged {
            onItemClickListener?.onItemClick(
                binding.etAmount,
                it,
                -1
            )
        }
        setFocusListener(binding.etAmount, questionUiFields)
        return binding.root
    }

    fun questionTypeDropDownItemViewHolder(
        binding: LayoutQuestionTypeDropDownBinding, questionUiFields: QuestionUiFields,
        position: Int,
        onItemClickListener: OnItemClickListener?
    ): View {
        binding.viewModel =
            QuestionnaireItemViewModel(
                questionUiFields,
                position,
                onItemClickListener
            )
        return binding.root
    }

    fun questionTypeCountriesItemViewHolder(
        binding: LayoutQuestionTypeCountriesBinding, questionUiFields: QuestionUiFields,
        position: Int,
        onItemClickListener: OnItemClickListener?
    ): View {
        val businessAdapter: BusinessCountriesAdapter by lazy {
            BusinessCountriesAdapter(arrayListOf())
        }

        businessAdapter.setList(
            viewModel.selectedBusinessCountries.get() ?: arrayListOf()
        )
        binding.businessCountriesAdapter = businessAdapter
        binding.viewModel =
            QuestionnaireItemViewModel(
                questionUiFields,
                position,
                onItemClickListener
            )
        return binding.root
    }

    fun setFocusListener(input: AppCompatEditText, questionUiFields: QuestionUiFields) {
        input.setOnFocusChangeListener { v, hasFocus ->
            questionUiFields.isFocusInput.set(hasFocus)
        }
    }

    fun getLayoutIdForViewType(viewType: Int): Int = viewType

    fun getItemViewType(position: Int): Int {
        return getLayoutId(viewModel.questionsList[position].question.questionType)
    }

    fun getViewFromBinding(
        binding: ViewDataBinding,
        questionUiField: QuestionUiFields,
        position: Int,
        onItemClickListener: OnItemClickListener?
    ): View? {
        return when (binding) {
            is LayoutQuestionTypeEditTextBinding -> {
                questionTypeEditTextItemViewHolder(
                    binding,
                    questionUiField,
                    position,
                    onItemClickListener
                )
            }
            is LayoutQuestionTypeEditTextWithAmountBinding -> {
                questionTypeEditTextWithAmountItemViewHolder(
                    binding,
                    questionUiField,
                    position,
                    onItemClickListener
                )
            }
            is LayoutQuestionTypeDropDownBinding -> {
                questionTypeDropDownItemViewHolder(
                    binding,
                    questionUiField,
                    position,
                    onItemClickListener
                )
            }
            is LayoutQuestionTypeCountriesBinding -> {
                questionTypeCountriesItemViewHolder(
                    binding,
                    questionUiField,
                    position,
                    onItemClickListener
                )
            }
            else -> null
        }
    }
}