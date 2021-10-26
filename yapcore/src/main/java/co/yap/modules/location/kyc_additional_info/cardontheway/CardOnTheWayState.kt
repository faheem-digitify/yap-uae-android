package co.yap.modules.location.kyc_additional_info.cardontheway

import androidx.databinding.ObservableField
import co.yap.networking.coreitems.CoreBottomSheetData
import co.yap.yapcore.BaseState

class CardOnTheWayState : BaseState(),
    ICardOnTheWay.State {
    override var bottomSheetItems: ObservableField<MutableList<CoreBottomSheetData>> =
        ObservableField(arrayListOf())
}