package co.yap.yapcore


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.Nullable
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import co.yap.yapcore.helpers.extentions.onClick
import co.yap.yapcore.interfaces.OnItemClickListener


abstract class BaseRVAdapter<T : Any, VM : BaseListItemViewModel<T>, VH : BaseViewHolder<T, VM>>
    (
    var datas: MutableList<T>,
    private var navigation: NavController?
) :
    RecyclerView.Adapter<VH>() {
    @Nullable
    var onItemClickListener: OnItemClickListener? = null
    var onChildViewClickListener: ((view: View, position: Int, data: T?) -> Unit)? =
        null

    override fun getItemCount() = datas.count()

    override fun onBindViewHolder(holder: VH, position: Int) {
        if (datas.size > position)
            holder.setItem(datas[position], position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val viewModel = createViewModel()
        val view = LayoutInflater.from(parent.context).inflate(getLayoutId(viewType), parent, false)
        val mDataBinding = DataBindingUtil.bind<ViewDataBinding>(view)
        mDataBinding?.setVariable(getVariableId(), viewModel)
        val holder: VH = getViewHolder(view, viewModel, mDataBinding!!, viewType)
        holder.onClick { view, position, type ->
            onItemClickListener?.onItemClick(
                view,
                datas[holder.adapterPosition],
                holder.adapterPosition
            )
        }
        return holder
    }

    protected fun createViewModel(): VM {
        val viewModel: VM = getViewModel()
        viewModel.onCreate(Bundle(), navigation)
        navigation?.let { onItemClickListener = viewModel }
        onChildViewClickListener?.let {
            viewModel.onChildViewClickListener = it
        }

        return viewModel
    }

    @LayoutRes
    abstract fun getLayoutId(viewType: Int): Int

    abstract fun getViewHolder(
        view: View,
        viewModel: VM,
        mDataBinding: ViewDataBinding,
        viewType: Int
    ): VH

    abstract fun getViewModel(): VM
    abstract fun getVariableId(): Int

    fun addAll(datas: List<T>) {
        this.datas.addAll(datas)
        notifyDataSetChanged()
    }

    fun setData(@Nullable newData: MutableList<T>) {
        if (this.datas !== newData) {
            this.datas = newData
        }
        notifyDataSetChanged()
    }

    fun add(type: T) {
        this.datas.add(type)
        notifyItemInserted(this.datas.size - 1)
        notifyDataSetChanged()
    }

    fun removeAll() {
        this.datas.clear()
        notifyDataSetChanged()
    }

    open fun remove(type: T) {
        val position = this.datas.indexOf(type)
        this.datas.remove(type)
        notifyItemRemoved(position)
        notifyDataSetChanged()
    }

    open fun removeAt(position: Int) {
        this.datas.removeAt(position)
        notifyItemRemoved(position)
    }

    fun change(newItem: T, oldItem: T) {
        val position = this.datas.indexOf(oldItem)
        this.datas[position] = newItem
        notifyItemChanged(position)
        notifyDataSetChanged()
    }

    fun update(item: T) {
        val position = this.datas.indexOf(item)
        this.datas[position] = item
        notifyItemChanged(position)

    }

    fun update(position: Int) {
        val item = this.datas[position]
        this.datas[position] = item
        notifyItemChanged(position)

    }
}