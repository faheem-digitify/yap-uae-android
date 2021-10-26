package co.yap.modules.dashboard.addionalinfo.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import co.yap.yapuae.BR
import co.yap.yapuae.R
import co.yap.yapuae.databinding.FragmentAdditionalInfoQuestionBinding
import co.yap.modules.dashboard.addionalinfo.interfaces.IAdditionalInfoQuestion
import co.yap.modules.dashboard.addionalinfo.viewmodels.AdditionalInfoQuestionViewModel
import co.yap.networking.customers.requestdtos.UploadAdditionalInfo
import co.yap.yapcore.helpers.extentions.afterTextChanged

class AdditionalInfoQuestionFragment :
    AdditionalInfoBaseFragment<IAdditionalInfoQuestion.ViewModel>(),
    IAdditionalInfoQuestion.View {
    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.fragment_additional_info_question

    override val viewModel: AdditionalInfoQuestionViewModel
        get() = ViewModelProviders.of(this).get(AdditionalInfoQuestionViewModel::class.java)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.showHeader(true)
        getBindings().etAnswer.editText.afterTextChanged {
            viewModel.state.valid.set(it.isNotBlank())
        }
    }

    override fun onToolBarClick(id: Int) {
        when (id) {
            R.id.btnNext -> {
                uploadAndMoveNext()
            }

            R.id.tvDoItLater -> {
                requireActivity().finish()
            }
        }
    }

    private fun uploadAndMoveNext() {
        viewModel.uploadAnswer(
            UploadAdditionalInfo(
                questionAnswer = getBindings().etAnswer.getInputText(),
                id = viewModel.parentViewModel?.state?.questionList?.firstOrNull()?.id.toString()
            )
        ) {
            viewModel.parentViewModel?.submitAdditionalInfo {
                viewModel.moveToNext()
                navigate(R.id.action_additionalInfoQuestion_to_additionalInfoComplete)
            }
        }
    }

    private fun getBindings(): FragmentAdditionalInfoQuestionBinding =
        viewDataBinding as FragmentAdditionalInfoQuestionBinding

    override fun onBackPressed(): Boolean {
        return true
    }
}