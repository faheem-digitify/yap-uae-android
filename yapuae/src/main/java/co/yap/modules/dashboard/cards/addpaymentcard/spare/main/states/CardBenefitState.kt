package co.yap.modules.dashboard.cards.addpaymentcard.spare.main.states

import androidx.databinding.Bindable
import co.yap.yapuae.BR
import co.yap.modules.dashboard.cards.addpaymentcard.models.BenefitsModel
import co.yap.modules.dashboard.cards.addpaymentcard.spare.main.interfaces.ICardBenefit
import co.yap.yapcore.BaseState

class CardBenefitState : BaseState(), ICardBenefit.State {

    @get:Bindable
    override var benefitsModel: BenefitsModel? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.benefitsModel)
        }
}