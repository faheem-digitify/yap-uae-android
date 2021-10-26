package co.yap.sendmoney.y2y.home.phonecontacts

import androidx.recyclerview.widget.RecyclerView
import co.yap.networking.customers.requestdtos.Contact
import co.yap.networking.customers.responsedtos.sendmoney.IBeneficiary
import co.yap.sendmoney.databinding.ItemContactsBinding
import co.yap.yapcore.interfaces.OnItemClickListener


class YapContactItemViewHolder(private val itemContactsBinding: ItemContactsBinding) :
    RecyclerView.ViewHolder(itemContactsBinding.root) {

    fun onBind(
        contact: IBeneficiary?,
        position: Int,
        onItemClickListener: OnItemClickListener?
    ) {
        itemContactsBinding.viewModel =
            YapContactItemViewModel(contact, position, onItemClickListener)
        itemContactsBinding.executePendingBindings()
    }
}