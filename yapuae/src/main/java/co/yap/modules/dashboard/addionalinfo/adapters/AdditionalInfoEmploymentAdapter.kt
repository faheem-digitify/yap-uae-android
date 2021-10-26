package co.yap.modules.dashboard.addionalinfo.adapters

import android.content.Context
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import co.yap.yapuae.R
import co.yap.yapuae.databinding.ItemAdditionalInfoEmploymentBinding
import co.yap.modules.dashboard.addionalinfo.model.AdditionalInfoEmployment
import co.yap.modules.dashboard.addionalinfo.viewmodels.AdditionalInfoEmploymentItemViewModel
import co.yap.yapcore.BaseBindingRecyclerAdapter
import co.yap.yapcore.interfaces.OnItemClickListener

class AdditionalInfoEmploymentAdapter(
    private val context: Context,
    private val list: MutableList<AdditionalInfoEmployment>
) : BaseBindingRecyclerAdapter<AdditionalInfoEmployment, AdditionalInfoEmploymentAdapter.ViewHolder>(
    list
) {


    override fun getLayoutIdForViewType(viewType: Int): Int =
        R.layout.item_additional_info_employment

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.onBind(list[position], position, onItemClickListener)
    }

    class ViewHolder(private val itemAdditionalInfoEmploymentBinding: ItemAdditionalInfoEmploymentBinding) :
        RecyclerView.ViewHolder(itemAdditionalInfoEmploymentBinding.root) {
        fun onBind(
            additionalInfoEmployment: AdditionalInfoEmployment,
            position: Int,
            onItemClickListener: OnItemClickListener?
        ) {

            itemAdditionalInfoEmploymentBinding.viewModel =
                AdditionalInfoEmploymentItemViewModel(
                    additionalInfoEmployment,
                    position,
                    onItemClickListener
                )
            if (additionalInfoEmployment.isSelected)
                itemAdditionalInfoEmploymentBinding.clEmployment.isActivated =
                    additionalInfoEmployment.isSelected
            itemAdditionalInfoEmploymentBinding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(binding: ViewDataBinding): ViewHolder {
        return ViewHolder(binding as ItemAdditionalInfoEmploymentBinding)
    }
}