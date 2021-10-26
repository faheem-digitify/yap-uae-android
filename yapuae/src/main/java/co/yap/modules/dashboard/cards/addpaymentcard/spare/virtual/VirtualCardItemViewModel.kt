package co.yap.modules.dashboard.cards.addpaymentcard.spare.virtual

import androidx.databinding.ObservableField
import co.yap.networking.cards.responsedtos.VirtualCardDesigns

class VirtualCardItemViewModel(
    val cardName: ObservableField<String>,
    val item: VirtualCardDesigns,
    val position: Int
)