package co.yap.widgets.advrecyclerview.pagination

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.yap.widgets.advrecyclerview.pagination.PaginatedRecyclerView.PaginationState

private const val TYPE_PLACEHOLDER = Int.MAX_VALUE - 50 // magic

private const val TYPE_ERROR = Int.MAX_VALUE - 100 // magic

class PaginatedAdapter(val originalAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var placeholderAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>? = null
    private var errorAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>? = null
    private var state: PaginationState? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val view = recyclerView as PaginatedRecyclerView
        placeholderAdapter =
            if (view.placeholderAdapter != null) view.placeholderAdapter else PaginatedRecyclerView.PlaceholderAdapter.DEFAULT
        errorAdapter =
            if (view.errorAdapter != null) view.errorAdapter else PaginatedRecyclerView.ErrorAdapter.DEFAULT
    }
    fun notifyStateChanged(state: PaginationState?) {
        this.state = state
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_PLACEHOLDER -> placeholderAdapter?.onCreateViewHolder(
                parent,
                viewType
            )!!
            TYPE_ERROR -> errorAdapter?.onCreateViewHolder(
                parent,
                viewType
            )!!
            else -> originalAdapter.onCreateViewHolder(parent, viewType)
        }
    }
    override fun getItemCount(): Int {
        return if (isState(
                PaginationState.LOADING,
                PaginationState.ERROR
            )
        ) originalAdapter.itemCount + 1 else originalAdapter.itemCount
    }

    override fun getItemViewType(position: Int): Int {
        return if (isErrorRow(position)) {
            TYPE_ERROR
        } else if (isPlaceholderRow(position)) {
            TYPE_PLACEHOLDER
        } else {
            originalAdapter.getItemViewType(position)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (isErrorRow(position)) {
            errorAdapter?.onBindViewHolder(holder, position)
        } else if (isPlaceholderRow(position)) {
            placeholderAdapter?.onBindViewHolder(holder, position)
        } else {
            originalAdapter.onBindViewHolder(holder, position)
        }
    }

    override fun getItemId(position: Int): Long {
        return if (isPlaceholderRow(position) || isErrorRow(position)) RecyclerView.NO_ID else originalAdapter.getItemId(
            position
        )
    }

    override fun setHasStableIds(hasStableIds: Boolean) {
        super.setHasStableIds(hasStableIds)
        originalAdapter.setHasStableIds(hasStableIds)
    }

    fun isPlaceholderRow(position: Int): Boolean {
        return isState(PaginationState.LOADING) && position == getPlaceholderRowPosition()
    }

    private fun getPlaceholderRowPosition(): Int {
        return if (isState(PaginationState.LOADING)) itemCount - 1 else -1
    }

    fun isErrorRow(position: Int): Boolean {
        return isState(PaginationState.ERROR) && position == getErrorRowPosition()
    }

    private fun getErrorRowPosition(): Int {
        return if (isState(PaginationState.ERROR)) itemCount - 1 else -1
    }

    private fun isState(vararg states: PaginationState): Boolean {
        for (state in states) {
            if (this.state == state) {
                return true
            }
        }
        return false
    }
}