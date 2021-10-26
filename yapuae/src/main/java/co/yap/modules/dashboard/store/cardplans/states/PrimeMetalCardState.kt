package co.yap.modules.dashboard.store.cardplans.states

import androidx.databinding.ObservableField
import co.yap.modules.dashboard.store.cardplans.CardPlans
import co.yap.modules.dashboard.store.cardplans.interfaces.IPrimeMetalCard
import co.yap.yapcore.BaseState

class PrimeMetalCardState : BaseState(), IPrimeMetalCard.State {
    override val cardPlans: ObservableField<CardPlans> = ObservableField()
    override val planToView: ObservableField<String> = ObservableField()
}