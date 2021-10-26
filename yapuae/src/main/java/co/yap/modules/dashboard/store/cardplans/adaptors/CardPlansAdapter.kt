package co.yap.modules.dashboard.store.cardplans.adaptors

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.navigation.NavController
import co.yap.yapuae.BR
import co.yap.modules.dashboard.store.cardplans.CardPlans
import co.yap.modules.dashboard.store.cardplans.viewmodels.CardPlansItemViewModel
import co.yap.yapcore.BaseRVAdapter
import co.yap.yapcore.BaseViewHolder

class CardPlansAdapter(mValue: MutableList<CardPlans>, navigation: NavController?) :
    BaseRVAdapter<CardPlans, CardPlansItemViewModel, CardPlansAdapter.CardPlansViewHolder>(
        mValue,
        navigation
    ) {


    override fun getLayoutId(viewType: Int): Int = getViewModel().layoutRes()
    override fun getViewModel(): CardPlansItemViewModel = CardPlansItemViewModel()
    override fun getVariableId(): Int = BR.cardPlansItemViewModel
    override fun getViewHolder(
        view: View,
        viewModel: CardPlansItemViewModel,
        mDataBinding: ViewDataBinding,
        viewType: Int
    ): CardPlansViewHolder = CardPlansViewHolder(
        view,
        viewModel,
        mDataBinding
    )

    class CardPlansViewHolder(
        view: View,
        viewModel: CardPlansItemViewModel,
        mDataBinding: ViewDataBinding
    ) :
        BaseViewHolder<CardPlans, CardPlansItemViewModel>(view, viewModel, mDataBinding)
}
