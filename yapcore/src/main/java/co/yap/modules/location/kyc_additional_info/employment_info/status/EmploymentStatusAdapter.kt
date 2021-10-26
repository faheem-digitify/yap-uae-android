package co.yap.modules.location.kyc_additional_info.employment_info.status

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import co.yap.yapcore.BaseBindingRecyclerAdapter
import co.yap.yapcore.R
import co.yap.yapcore.databinding.ItemEmploymentStatusSelectionBinding

class EmploymentStatusAdapter(
    private val list: MutableList<EmploymentStatusSelectionModel>
) :
    BaseBindingRecyclerAdapter<EmploymentStatusSelectionModel, RecyclerView.ViewHolder>(list) {
    override fun onCreateViewHolder(binding: ViewDataBinding): RecyclerView.ViewHolder {
        return EmploymentStatusSelectionViewHolder(
            binding as ItemEmploymentStatusSelectionBinding
        )
    }

    override fun getLayoutIdForViewType(viewType: Int): Int =
        R.layout.item_employment_status_selection

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        if (holder is EmploymentStatusSelectionViewHolder) {
            holder.onBind(position, list.get(position), onItemClickListener)
        }
    }
}
