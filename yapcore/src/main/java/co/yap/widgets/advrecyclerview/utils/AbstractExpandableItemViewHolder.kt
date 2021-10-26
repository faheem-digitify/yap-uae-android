package co.yap.widgets.advrecyclerview.utils

import android.view.View
import androidx.databinding.ViewDataBinding
import co.yap.networking.models.ApiResponse
import co.yap.widgets.advrecyclerview.expandable.ExpandableItemState
import co.yap.widgets.advrecyclerview.expandable.ExpandableItemViewHolder
import co.yap.widgets.advrecyclerview.expandable.annotation.ExpandableItemStateFlags
import co.yap.yapcore.BaseListItemViewModel
import co.yap.yapcore.BaseViewHolder

open class AbstractExpandableItemViewHolder<ITEM : ApiResponse, VM : BaseListItemViewModel<ITEM>>
    (view: View, viewModel: VM, private val mDataBinding: ViewDataBinding) :
    BaseViewHolder<ITEM, VM>(view, viewModel, mDataBinding), ExpandableItemViewHolder {
    private val mExpandState = ExpandableItemState()

    /**
     * {@inheritDoc}
     */
    override fun setExpandStateFlags(@ExpandableItemStateFlags flags: Int) {
        mExpandState.flags = flags
    }

    /**
     * {@inheritDoc}
     */
    @ExpandableItemStateFlags
    override fun getExpandStateFlags(): Int {
        return mExpandState.flags
    }

    /**
     * {@inheritDoc}
     */
    override fun getExpandState(): ExpandableItemState {
        return mExpandState
    }
}