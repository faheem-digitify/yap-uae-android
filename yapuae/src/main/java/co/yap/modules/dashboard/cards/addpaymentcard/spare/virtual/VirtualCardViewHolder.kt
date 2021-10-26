package co.yap.modules.dashboard.cards.addpaymentcard.spare.virtual

import androidx.databinding.ObservableField
import androidx.recyclerview.widget.RecyclerView
import co.yap.yapuae.R
import co.yap.yapuae.databinding.ItemVirtualCardBinding
import co.yap.networking.cards.responsedtos.VirtualCardDesigns
import co.yap.yapcore.helpers.extentions.dimen
import co.yap.yapcore.helpers.extentions.loadCardImage

class VirtualCardViewHolder(private val itemYapVirtualCardBinding: ItemVirtualCardBinding) :
    RecyclerView.ViewHolder(itemYapVirtualCardBinding.root) {

    init {
        val binding = itemYapVirtualCardBinding
        val params = binding.ivCard.layoutParams
        params.width = binding.ivCard.context.dimen(R.dimen._204sdp)
        params.height = binding.ivCard.context.dimen(R.dimen._225sdp)
        binding.ivCard.layoutParams = params
    }

    fun onBind(
        position: Int,
        virtualCard: VirtualCardDesigns,
        cardName: ObservableField<String>
    ) {
        itemYapVirtualCardBinding.viewModel =
            VirtualCardItemViewModel(cardName, virtualCard, position)

        itemYapVirtualCardBinding.ivCard.loadCardImage(virtualCard.frontSideDesignImage)
        itemYapVirtualCardBinding.executePendingBindings()
    }
}
