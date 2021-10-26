package co.yap.modules.dashboard.addionalinfo.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import co.yap.yapuae.BR
import co.yap.yapuae.R
import co.yap.modules.dashboard.addionalinfo.interfaces.IAdditionalInfoEmployment
import co.yap.modules.dashboard.addionalinfo.viewmodels.AdditionalInfoEmploymentViewModel
import co.yap.yapcore.interfaces.OnItemClickListener

class AdditionalInfoEmploymentFragment :
    AdditionalInfoBaseFragment<IAdditionalInfoEmployment.ViewModel>(),
    IAdditionalInfoEmployment.View {
    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.fragment_additional_info_employment
    private var oldSelectedPosition = -1
    override val viewModel: IAdditionalInfoEmployment.ViewModel
        get() = ViewModelProviders.of(this).get(AdditionalInfoEmploymentViewModel::class.java)

    override fun onToolBarClick(id: Int) {
        when (id) {
            R.id.btnNext -> {
                viewModel.moveToNext()
                navigate(R.id.action_additionalInfoEmployment_to_additionalInfoQuestion)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        viewModel.additionalInfoEmploymentAdapter.allowFullItemClickListener = true
        viewModel.additionalInfoEmploymentAdapter.setItemListener(listener)
    }

    private val listener = object : OnItemClickListener {
        override fun onItemClick(view: View, data: Any, pos: Int) {
//            if (data is AdditionalDocument) {
            if (oldSelectedPosition != pos) {
                if (oldSelectedPosition != -1) {
                    viewModel.additionalInfoEmploymentAdapter.getDataList()[oldSelectedPosition].isSelected =
                        !viewModel.additionalInfoEmploymentAdapter.getDataList()[oldSelectedPosition].isSelected
                    viewModel.additionalInfoEmploymentAdapter.notifyItemChanged(
                        oldSelectedPosition
                    )

                }
                viewModel.additionalInfoEmploymentAdapter.getDataList()[pos].isSelected =
                    !viewModel.additionalInfoEmploymentAdapter.getDataList()[pos].isSelected
                viewModel.additionalInfoEmploymentAdapter.notifyItemChanged(pos)
                oldSelectedPosition = pos
            }
//            }
        }
    }

    override fun onBackPressed(): Boolean {
        return false
    }
}