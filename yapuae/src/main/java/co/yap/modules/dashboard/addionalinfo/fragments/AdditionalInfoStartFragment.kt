package co.yap.modules.dashboard.addionalinfo.fragments

import androidx.lifecycle.ViewModelProviders
import co.yap.yapuae.BR
import co.yap.yapuae.R
import co.yap.yapuae.databinding.FragmentAdditionalInfoStartBinding
import co.yap.modules.dashboard.addionalinfo.interfaces.IAdditionalInfoStart
import co.yap.modules.dashboard.addionalinfo.viewmodels.AdditionalInfoStartViewModel
import co.yap.networking.customers.models.additionalinfo.AdditionalQuestion
import co.yap.yapcore.enums.AdditionalInfoScreenType

class AdditionalInfoStartFragment : AdditionalInfoBaseFragment<IAdditionalInfoStart.ViewModel>(),
    IAdditionalInfoStart.View {
    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.fragment_additional_info_start

    override val viewModel: AdditionalInfoStartViewModel
        get() = ViewModelProviders.of(this).get(AdditionalInfoStartViewModel::class.java)

    private fun getBindings(): FragmentAdditionalInfoStartBinding =
        viewDataBinding as FragmentAdditionalInfoStartBinding

    override fun onToolBarClick(id: Int) {
        when (id) {
            R.id.btnNext -> {
                moveNextScreen()
            }
            R.id.tvDoItLater -> {
                requireActivity().finish()
            }
        }
    }

    private fun moveNextScreen() {
        when (viewModel.getScreenType()) {
            AdditionalInfoScreenType.BOTH_SCREENS.name -> {
                navigate(R.id.action_additionalInfoStartFragment_to_selectDocumentFragment)
            }
            AdditionalInfoScreenType.DOCUMENT_SCREEN.name -> {
                navigate(R.id.action_additionalInfoStartFragment_to_selectDocumentFragment)
            }
            AdditionalInfoScreenType.QUESTION_SCREEN.name -> {
                navigate(R.id.action_additionalInfoStartFragment_to_additionalInfoQuestion)
            }
            AdditionalInfoScreenType.SUCCESS_SCREEN.name -> {
                navigate(R.id.action_additionalInfoStartFragment_to_additionalInfoComplete)
            }
        }
    }

    override fun onBackPressed(): Boolean {
        return true
    }
}