package co.yap.widgets.bottomsheet

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import co.yap.networking.coreitems.CoreBottomSheetData
import co.yap.yapcore.BR
import co.yap.yapcore.R
import co.yap.yapcore.constants.Constants
import co.yap.yapcore.databinding.LayoutBottomSheetBinding
import co.yap.yapcore.helpers.extentions.afterTextChanged
import co.yap.yapcore.helpers.extentions.getScreenHeight
import co.yap.yapcore.interfaces.OnItemClickListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


open class CoreBottomSheet(
    private val mListener: OnItemClickListener?,
    private val bottomSheetItems: MutableList<CoreBottomSheetData>,
    private val viewType: Int = Constants.VIEW_WITHOUT_FLAG,
    private val configuration: BottomSheetConfiguration
) : BottomSheetDialogFragment(), ICoreBottomSheet.View {
    lateinit var viewDataBinding: ViewDataBinding
    override val viewModel: CoreBottomSheetViewModel
        get() = ViewModelProviders.of(this).get(CoreBottomSheetViewModel::class.java)

    open val adapter: CoreBottomSheetAdapter by lazy {
        CoreBottomSheetAdapter(bottomSheetItems, viewType)
    }

    override fun getTheme(): Int = R.style.AppBottomSheetDialogTheme

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding =
            DataBindingUtil.inflate(inflater, R.layout.layout_bottom_sheet, container, false)
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        viewDataBinding.setVariable(BR.viewModel, viewModel)
        viewDataBinding.executePendingBindings()
        val adapter = CoreBottomSheetAdapter(bottomSheetItems, viewType)
        adapter.onItemClickListener = myListener
        adapter.allowFullItemClickListener = true
//        viewModel.state.searchBarVisibility.set(viewType == Constants.VIEW_WITH_FLAG)
        viewModel.state.searchBarVisibility.set(configuration.showSearch)
        viewModel.state.headerSeparatorVisibility.set(configuration.showHeaderSeparator ?: false)
        configuration.heading?.let {
            getBinding().tvlabel.text = it
        }
        getBinding().lySearchView.etSearch.afterTextChanged {
            adapter.filter.filter(it) { itemCount ->
                if (itemCount == 0) {
                    viewModel.state.noItemFound.set(true)
                } else {
                    viewModel.state.noItemFound.set(false)
                }
            }
        }
        getBinding().rvBottomSheet.layoutManager = LinearLayoutManager(context)
        val params = getBinding().rvBottomSheet.layoutParams as ConstraintLayout.LayoutParams
        params.height =
            if (viewType == Constants.VIEW_WITH_FLAG || viewType == Constants.VIEW_FIXED_HEIGHT) (getScreenHeight() / 2) + 100 else params.height
        getBinding().rvBottomSheet.layoutParams = params
        getBinding().rvBottomSheet.adapter = adapter
    }

    private val myListener: OnItemClickListener = object : OnItemClickListener {
        override fun onItemClick(view: View, data: Any, pos: Int) {
            this@CoreBottomSheet.dismiss()
            mListener?.onItemClick(view, data, pos)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog =
            super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        bottomSheetDialog.setOnShowListener { dialog ->
            bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        return bottomSheetDialog
    }

    override fun showLoader(isVisible: Boolean) {
    }

    override fun showToast(msg: String) {
    }

    override fun showInternetSnack(isVisible: Boolean) {
    }

    override fun isPermissionGranted(permission: String): Boolean {
        return true
    }

    override fun requestPermissions() {
    }

    override fun getString(resourceKey: String): String {
        return ""
    }

    override fun onNetworkStateChanged(isConnected: Boolean) {
    }

    private fun getBinding() = viewDataBinding as LayoutBottomSheetBinding
    override fun getScreenName(): String? = null

}