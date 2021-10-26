package co.yap.modules.dashboard.addionalinfo.fragments

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.yap.yapuae.BR
import co.yap.yapuae.R
import co.yap.yapuae.databinding.FragmentAdditionalInfoViewDocumentBinding
import co.yap.modules.dashboard.addionalinfo.interfaces.IAdditionalInfoViewDocument
import co.yap.modules.dashboard.addionalinfo.model.AdditionalDocumentImage
import co.yap.modules.dashboard.addionalinfo.viewmodels.AdditionalInfoViewDocumentViewModel
import co.yap.yapcore.helpers.ImageBinding

class AdditionalInfoViewDocumentFragment :
    AdditionalInfoBaseFragment<IAdditionalInfoViewDocument.ViewModel>(),
    IAdditionalInfoViewDocument.View {
    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.fragment_additional_info_view_document

    override val viewModel: IAdditionalInfoViewDocument.ViewModel
        get() = ViewModelProviders.of(this).get(AdditionalInfoViewDocumentViewModel::class.java)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getDataArguments()
        addObservers()
    }

    private fun getDataArguments() {
        arguments?.let { bundle ->
            bundle.getParcelable<AdditionalDocumentImage>(AdditionalDocumentImage::class.java.name)
                ?.let {
                    ImageBinding.setCircularImageUrl(getBindings().ivReviewImage, it.file.toUri())
                    viewModel.state.documentName.set(it.name)
                }
        }
    }

    private fun addObservers() {
        viewModel.clickEvent.observe(this, clickListener)
    }

    private val clickListener = Observer<Int> {
        when (it) {
            R.id.btnUpload -> {
                setResult()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.clickEvent.removeObserver(clickListener)
    }

    private fun setResult() {
        activity?.setResult(Activity.RESULT_OK)
        activity?.finish()
    }

    override fun onToolBarClick(id: Int) {
        when (id) {
            R.id.ivClose -> {
                requireActivity().onBackPressed()
            }
        }
    }

    private fun getBindings(): FragmentAdditionalInfoViewDocumentBinding {
        return viewDataBinding as FragmentAdditionalInfoViewDocumentBinding
    }
}