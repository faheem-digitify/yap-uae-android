package co.yap.sendmoney.y2y.home.yapcontacts

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import co.yap.networking.customers.responsedtos.sendmoney.IBeneficiary
import co.yap.sendmoney.R
import co.yap.sendmoney.databinding.ItemContactsBinding
import co.yap.sendmoney.y2y.home.phonecontacts.YapContactItemViewHolder
import co.yap.yapcore.BaseBindingSearchRecylerAdapter

class YapContactsAdaptor(private val list: MutableList<IBeneficiary>) :
    BaseBindingSearchRecylerAdapter<IBeneficiary, RecyclerView.ViewHolder>(list) {

    override fun getLayoutIdForViewType(viewType: Int): Int = R.layout.item_contacts

    override fun onCreateViewHolder(binding: ViewDataBinding): RecyclerView.ViewHolder {
        return YapContactItemViewHolder(binding as ItemContactsBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        if (holder is YapContactItemViewHolder) {
            holder.onBind(list[position], position, onItemClickListener)
        }
    }

    override fun filterItem(constraint: CharSequence?, item: IBeneficiary): Boolean {
        val filterString = constraint.toString().toLowerCase()
        val filterableString = item.subtitle ?: ""
        val filterableStringForName = item.fullName ?: ""
        return (filterableString.toLowerCase()
            .contains(filterString) || filterableStringForName.toLowerCase().contains(filterString))

    }

    override fun getItemViewType(position: Int): Int = position
}
