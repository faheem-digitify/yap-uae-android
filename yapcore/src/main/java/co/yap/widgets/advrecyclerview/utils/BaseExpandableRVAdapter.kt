package co.yap.widgets.advrecyclerview.utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import co.yap.networking.models.ApiResponse
import co.yap.widgets.advrecyclerview.expandable.ExpandableItemAdapter
import co.yap.yapcore.BaseListItemViewModel

abstract class BaseExpandableRVAdapter<CT : ApiResponse, CVM : BaseListItemViewModel<CT>, CVH : AbstractExpandableItemViewHolder<CT, CVM>,
        GT : ApiResponse, GVM : BaseListItemViewModel<GT>, GVH : AbstractExpandableItemViewHolder<GT, GVM>> :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), ExpandableItemAdapter<GVH, CVH> {

    /**
     * This method will not be called.
     * Override [onCreateGroupViewHolder] and
     * [onCreateChildViewHolder] instead.
     *
     * @param parent   not used
     * @param viewType not used
     * @return null
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        throw IllegalStateException("This method should not be called")
    }

    /**
     * This method will not be called.
     * Override [onBindGroupViewHolder] ()} and
     * [onBindChildViewHolder] instead.
     *
     * @param holder not used
     * @param position not used
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {}

    override fun onCreateGroupViewHolder(parent: ViewGroup?, viewType: Int): GVH {
        val viewModel = createGroupViewModel(viewType)
        val view =
            LayoutInflater.from(parent?.context).inflate(getGroupLayoutId(viewType), parent, false)
        val mDataBinding = DataBindingUtil.bind<ViewDataBinding>(view)
        mDataBinding?.setVariable(getGroupVariableId(), viewModel)
        return getGroupViewHolder(view, viewModel, mDataBinding!!, viewType)
    }

    private fun createGroupViewModel(viewType: Int): GVM {
        val viewModel: GVM = getGroupViewModel(viewType)
        viewModel.onCreate(Bundle(), null)
//        navigation?.let { onItemClickListener = viewModel }
        return viewModel
    }

    override fun onCreateChildViewHolder(parent: ViewGroup?, viewType: Int): CVH {
        val viewModel = createChildViewModel(viewType)
        val view =
            LayoutInflater.from(parent?.context).inflate(getChildLayoutId(viewType), parent, false)
        val mDataBinding = DataBindingUtil.bind<ViewDataBinding>(view)
        mDataBinding?.setVariable(getChildVariableId(), viewModel)
        return getChildViewHolder(view, viewModel, mDataBinding!!, viewType)
    }

    private fun createChildViewModel(viewType: Int): CVM {
        val viewModel = getChildViewModel(viewType)
        viewModel.onCreate(Bundle(), null)
//        navigation?.let { onItemClickListener = viewModel }
        return viewModel
    }

    /**
     * This method will not be called.
     * Override [getGroupCount] and [getChildCount] instead.
     *
     * @return 0
     */
    override fun getItemCount(): Int {
        return 0
    }

    override fun onBindGroupViewHolder(
        holder: GVH,
        groupPosition: Int,
        viewType: Int,
        payloads: MutableList<Any>?
    ) {
        onBindGroupViewHolder(holder, groupPosition, viewType)
    }

    override fun onBindChildViewHolder(
        holder: CVH,
        groupPosition: Int,
        childPosition: Int,
        viewType: Int,
        payloads: MutableList<Any>?
    ) {
        onBindChildViewHolder(holder, groupPosition, childPosition, viewType)
    }

    override fun onCheckCanExpandOrCollapseGroup(
        holder: GVH,
        groupPosition: Int,
        x: Int,
        y: Int,
        expand: Boolean
    ) = true

    override fun onHookGroupExpand(groupPosition: Int, fromUser: Boolean) = true

    override fun onHookGroupExpand(groupPosition: Int, fromUser: Boolean, payload: Any?) =
        onHookGroupExpand(groupPosition, fromUser)

    override fun onHookGroupCollapse(groupPosition: Int, fromUser: Boolean) = false

    override fun onHookGroupCollapse(groupPosition: Int, fromUser: Boolean, payload: Any?) =
        onHookGroupCollapse(groupPosition, fromUser)

    override fun getInitialGroupExpandedState(groupPosition: Int) = false

    /**
     * This method will not be called.
     * Override [.getGroupId] and [.getChildId] instead.
     *
     * @param position not used
     * @return [RecyclerView.NO_ID]
     */
    override fun getItemId(position: Int) = RecyclerView.NO_ID
    override fun getItemViewType(position: Int) = position

    abstract fun getChildViewHolder(
        view: View,
        viewModel: CVM,
        mDataBinding: ViewDataBinding,
        viewType: Int
    ): CVH


    abstract fun getGroupViewHolder(
        view: View,
        viewModel: GVM,
        mDataBinding: ViewDataBinding,
        viewType: Int
    ): GVH

    @LayoutRes
    abstract fun getChildLayoutId(viewType: Int): Int

    @LayoutRes
    abstract fun getGroupLayoutId(viewType: Int): Int
    abstract fun getGroupViewModel(viewType: Int): GVM
    abstract fun getGroupVariableId(): Int
    abstract fun getChildViewModel(viewType: Int): CVM
    abstract fun getChildVariableId(): Int
}
