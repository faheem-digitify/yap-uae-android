package co.yap.widgets.bottomsheet.multi_selection_bottom_sheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import co.yap.widgets.bottomsheet.CoreBottomSheet
import co.yap.networking.coreitems.CoreBottomSheetData
import co.yap.widgets.bottomsheet.BottomSheetConfiguration
import co.yap.yapcore.BR
import co.yap.yapcore.R
import co.yap.yapcore.constants.Constants
import co.yap.yapcore.databinding.LayoutMultiSelectorBottomSheetBinding
import co.yap.yapcore.helpers.extentions.afterTextChanged
import co.yap.yapcore.helpers.extentions.generateChipViews
import co.yap.yapcore.helpers.extentions.getScreenHeight
import co.yap.yapcore.interfaces.OnItemClickListener
import kotlinx.android.synthetic.main.layout_core_bottomsheet_search.*

class CoreMultiSelectionBottomSheet(
    private val mListener: OnItemClickListener?,
    private val bottomSheetItems: MutableList<CoreBottomSheetData>,
    private val configuration: BottomSheetConfiguration,
    private val viewType: Int = Constants.VIEW_WITHOUT_FLAG
) : CoreBottomSheet(mListener, bottomSheetItems, viewType,configuration) {
    override val adapter: CoreMultiSelectionBottomSheetAdapter by lazy {
        CoreMultiSelectionBottomSheetAdapter(bottomSheetItems, viewType)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewDataBinding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.layout_multi_selector_bottom_sheet,
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
        adapter.onItemClickListener = myListener
        adapter.allowFullItemClickListener = true
        viewModel.state.searchBarVisibility.set(viewType != Constants.VIEW_WITHOUT_FLAG)
        configuration.heading?.let {
            getBinding().tvlabel.text = it
        }
        etSearch.afterTextChanged {
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
            if (viewType == Constants.VIEW_WITH_FLAG) (getScreenHeight() / 2) + 100 else params.height
        getBinding().rvBottomSheet.layoutParams = params
        getBinding().rvBottomSheet.adapter = adapter
        viewModel.selectedViewsList.clear()
        bottomSheetItems.filter { it.isSelected == true }.forEach {
            viewModel.selectedViewsList.add(it.subTitle ?: "")
        }
        generateChipViews(viewModel.selectedViewsList)
    }

    private val myListener: OnItemClickListener = object : OnItemClickListener {
        override fun onItemClick(view: View, data: Any, pos: Int) {
            if (data is CoreBottomSheetData) {
                data.isSelected = !(data.isSelected ?: false)
                adapter.setItemAt(pos, data)
                if (data.isSelected == true) {
                    viewModel.selectedViewsList.add(data.subTitle ?: "")
                } else {
                    viewModel.selectedViewsList.remove(data.subTitle)
                }
                getBinding().chipGroup.removeAllViews()

                generateChipViews(selectedList = viewModel.selectedViewsList)
                getBinding().horizontalChips.post {
                    getBinding().horizontalChips.fullScroll(View.FOCUS_RIGHT)
                }
            }
            mListener?.onItemClick(view, viewModel.selectedViewsList, pos)
        }
    }

    private fun generateChipViews(selectedList: List<String>) {
        getBinding().chipGroup.generateChipViews(
            R.layout.item_selected_country_chip,
            selectedList
        )
    }

    private fun getBinding() = viewDataBinding as LayoutMultiSelectorBottomSheetBinding
}
