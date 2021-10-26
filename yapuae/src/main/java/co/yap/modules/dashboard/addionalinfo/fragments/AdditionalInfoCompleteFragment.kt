package co.yap.modules.dashboard.addionalinfo.fragments

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import co.yap.yapuae.BR
import co.yap.yapuae.R
import co.yap.yapuae.databinding.FragmentAdditionalInfoCompleteBinding
import co.yap.modules.dashboard.addionalinfo.interfaces.IAdditionalInfoComplete
import co.yap.modules.dashboard.addionalinfo.viewmodels.AdditionalInfoCompleteViewModel

class AdditionalInfoCompleteFragment :
    AdditionalInfoBaseFragment<IAdditionalInfoComplete.ViewModel>(),
    IAdditionalInfoComplete.View {
    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.fragment_additional_info_complete

    override val viewModel: AdditionalInfoCompleteViewModel
        get() = ViewModelProviders.of(this).get(AdditionalInfoCompleteViewModel::class.java)

    private fun getBindings(): FragmentAdditionalInfoCompleteBinding =
        viewDataBinding as FragmentAdditionalInfoCompleteBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    override fun onToolBarClick(id: Int) {
        when (id) {
            R.id.btnNext -> {
                setResultFinish()
            }
        }
    }

    private fun setResultFinish() {
        requireActivity().setResult(Activity.RESULT_OK)
        requireActivity().finish()
    }

    override fun onBackPressed(): Boolean {
        return true
    }
}