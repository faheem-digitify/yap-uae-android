package co.yap.widgets.bottomsheet.bottomsheet_with_initials

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import co.yap.networking.coreitems.CoreBottomSheetData
import co.yap.widgets.bottomsheet.BottomSheetConfiguration
import co.yap.widgets.bottomsheet.CoreBottomSheet
import co.yap.yapcore.BR
import co.yap.yapcore.R
import co.yap.yapcore.constants.Constants
import co.yap.yapcore.databinding.LayoutInitialsBottomSheetBinding
import co.yap.yapcore.helpers.extentions.getScreenHeight
import co.yap.yapcore.interfaces.OnItemClickListener

class CoreInitialsBottomSheet(
    private val mListener: OnItemClickListener?,
    private val bottomSheetItems: MutableList<CoreBottomSheetData>,
    private val configuration: BottomSheetConfiguration,
    private val viewType: Int = Constants.VIEW_WITHOUT_FLAG
) : CoreBottomSheet(mListener, bottomSheetItems, viewType, configuration) {
    override val adapter: CoreInitialsBottomSheetAdapter by lazy {
        CoreInitialsBottomSheetAdapter(bottomSheetItems, viewType)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewDataBinding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.layout_initials_bottom_sheet,
                container,
                false
            )
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews()
    }

    private fun initViews() {
        viewDataBinding.setVariable(BR.viewModel, viewModel)
        viewDataBinding.executePendingBindings()
        adapter.onItemClickListener = mListener
        adapter.allowFullItemClickListener = true
        configuration.heading?.let {
            getBinding().tvHeadingTitle.text = it
        }
        configuration.subHeading?.let {
            getBinding().tvHeadingSubTitle.text = it
        }
        getBinding().rvBottomSheet.layoutManager = LinearLayoutManager(context)
        val params = getBinding().rvBottomSheet.layoutParams as ConstraintLayout.LayoutParams
        params.height =
            if (viewType == Constants.VIEW_FIXED_HEIGHT) (getScreenHeight() / 2) + 100 else params.height
        getBinding().rvBottomSheet.layoutParams = params
        getBinding().rvBottomSheet.adapter = adapter
    }

    private fun getBinding() = viewDataBinding as LayoutInitialsBottomSheetBinding
}