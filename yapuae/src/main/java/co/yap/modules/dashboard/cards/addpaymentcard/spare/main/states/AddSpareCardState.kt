package co.yap.modules.dashboard.cards.addpaymentcard.spare.main.states

import androidx.databinding.Bindable
import androidx.databinding.ObservableField
import co.yap.yapuae.BR
import co.yap.modules.dashboard.cards.addpaymentcard.spare.main.interfaces.IAddSpareCard
import co.yap.yapcore.BaseState

class AddSpareCardState : BaseState(), IAddSpareCard.State {

    val VISIBLE: Int = 0x00000000
    val GONE: Int = 0x00000008

    @get:Bindable
    override var cardType: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.cardType)
        }

    @get:Bindable
    override var cardName: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.cardName)
        }

    @get:Bindable
    override var virtualCardFee: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.virtualCardFee)
        }

    @get:Bindable
    override var coreButtonText: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.coreButtonText)
        }
    override var availableBalance: ObservableField<CharSequence> = ObservableField()
}