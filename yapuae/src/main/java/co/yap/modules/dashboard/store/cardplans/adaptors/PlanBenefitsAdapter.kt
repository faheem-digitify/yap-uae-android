package co.yap.modules.dashboard.store.cardplans.adaptors

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.navigation.NavController
import co.yap.yapuae.BR
import co.yap.modules.dashboard.store.cardplans.CardBenefits
import co.yap.modules.dashboard.store.cardplans.viewmodels.PlanBenefitsItemViewModel
import co.yap.yapcore.BaseRVAdapter
import co.yap.yapcore.BaseViewHolder

class PlanBenefitsAdapter(mValue: MutableList<CardBenefits>, navigation: NavController?) :
    BaseRVAdapter<CardBenefits, PlanBenefitsItemViewModel, PlanBenefitsAdapter.PlanBenefitsViewHolder>(
        mValue,
        navigation
    ) {
    override fun getLayoutId(viewType: Int): Int = getViewModel().layoutRes()
    override fun getViewHolder(
        view: View,
        viewModel: PlanBenefitsItemViewModel,
        mDataBinding: ViewDataBinding,
        viewType: Int
    ): PlanBenefitsViewHolder = PlanBenefitsViewHolder(
        view,
        viewModel,
        mDataBinding
    )

    override fun getViewModel(): PlanBenefitsItemViewModel = PlanBenefitsItemViewModel()

    override fun getVariableId(): Int = BR.planBenefitsItemViewModel
    class PlanBenefitsViewHolder(
        view: View,
        viewModel: PlanBenefitsItemViewModel,
        mDataBinding: ViewDataBinding
    ) :
        BaseViewHolder<CardBenefits, PlanBenefitsItemViewModel>(view, viewModel, mDataBinding)
}