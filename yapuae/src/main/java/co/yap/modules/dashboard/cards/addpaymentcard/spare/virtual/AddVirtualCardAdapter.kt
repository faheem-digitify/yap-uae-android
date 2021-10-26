package co.yap.modules.dashboard.cards.addpaymentcard.spare.virtual

import androidx.databinding.ObservableField
import androidx.databinding.ViewDataBinding
import co.yap.yapuae.R
import co.yap.yapuae.databinding.ItemVirtualCardBinding
import co.yap.networking.cards.responsedtos.VirtualCardDesigns
import co.yap.yapcore.BaseBindingRecyclerAdapter

class AddVirtualCardAdapter(private val list: MutableList<VirtualCardDesigns>) :
    BaseBindingRecyclerAdapter<VirtualCardDesigns, VirtualCardViewHolder>(list) {

    var cardName: ObservableField<String> = ObservableField()
    override fun onCreateViewHolder(binding: ViewDataBinding): VirtualCardViewHolder {
        return VirtualCardViewHolder(binding as ItemVirtualCardBinding)
    }

    override fun onBindViewHolder(holder: VirtualCardViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.onBind(position, list[position], cardName)
    }

    override fun getLayoutIdForViewType(viewType: Int): Int = R.layout.item_virtual_card
}
