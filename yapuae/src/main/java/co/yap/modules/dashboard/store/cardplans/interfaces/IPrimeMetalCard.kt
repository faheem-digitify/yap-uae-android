package co.yap.modules.dashboard.store.cardplans.interfaces

import androidx.databinding.ObservableField
import co.yap.yapuae.databinding.FragmentPrimeMetalCardBinding
import co.yap.modules.dashboard.store.cardplans.CardBenefits
import co.yap.modules.dashboard.store.cardplans.CardPlans
import co.yap.modules.dashboard.store.cardplans.adaptors.PlanBenefitsAdapter
import co.yap.yapcore.IBase

interface IPrimeMetalCard {
    interface View : IBase.View<ViewModel> {
        fun getBindings(): FragmentPrimeMetalCardBinding
        fun initVideoView()
    }

    interface ViewModel : IBase.ViewModel<State> {
        fun getCardPlan(tag: String): CardPlans?
        fun getCardBenefits(tag: String): MutableList<CardBenefits>
        var planBenefitsAdapter: PlanBenefitsAdapter
    }

    interface State : IBase.State {
        val cardPlans: ObservableField<CardPlans>
        val planToView: ObservableField<String>
    }
}