package co.yap.modules.dashboard.store.cardplans.viewmodels

import android.app.Application
import co.yap.modules.dashboard.store.cardplans.CardBenefits
import co.yap.modules.dashboard.store.cardplans.CardPlans
import co.yap.modules.dashboard.store.cardplans.adaptors.PlanBenefitsAdapter
import co.yap.modules.dashboard.store.cardplans.interfaces.IPrimeMetalCard
import co.yap.modules.dashboard.store.cardplans.states.PrimeMetalCardState
import co.yap.yapcore.constants.Constants

class PrimeMetalCardViewModel(application: Application) :
    CardPlansBaseViewModel<IPrimeMetalCard.State>(application), IPrimeMetalCard.ViewModel {
    override val state: PrimeMetalCardState = PrimeMetalCardState()
    override var planBenefitsAdapter: PlanBenefitsAdapter = PlanBenefitsAdapter(arrayListOf(), null)

    override fun onCreate() {
        super.onCreate()
        parentViewModel?.selectedPlan?.get()?.let { planTag ->
            planBenefitsAdapter.setData(getCardBenefits(planTag))
            state.cardPlans.set(getCardPlan(planTag))
        }
    }

    override fun getCardPlan(tag: String): CardPlans? {
        return when (tag) {
            Constants.PRIME_CARD_PLAN -> {
                parentViewModel?.cards?.get(0)
            }
            Constants.METAL_CARD_PLAN -> {
                parentViewModel?.cards?.get(1)
            }
            else -> parentViewModel?.cards?.get(0)
        }
    }

    override fun getCardBenefits(tag: String): MutableList<CardBenefits> {
        return when (tag) {
            Constants.PRIME_CARD_PLAN -> {
                arrayListOf(
                    CardBenefits("Exclusive partner offers"),
                    CardBenefits("2 free virtual cards"),
                    CardBenefits("Unlimited multi-currency wallets with real-time exchange rates"),
                    CardBenefits("1 free international transfer per month to 40 countries"),
                    CardBenefits("Hold up to 3 physical cards"),
                    CardBenefits("1 free young subscription"),
                    CardBenefits("1 free household subscription"),
                    CardBenefits("Priority customer support"),
                    CardBenefits("Mastercard Platinum benefits", isLast = true))
            }
            Constants.METAL_CARD_PLAN -> {
                arrayListOf(
                    CardBenefits("Exclusive partner offers"),
                    CardBenefits("4 free virtual cards"),
                    CardBenefits("Unlimited multi-currency wallets with real-time exchange rates"),
                    CardBenefits("1 free international transfer per month to 40 countries"),
                    CardBenefits("Hold up to 5 physical cards"),
                    CardBenefits("Premier airport lounge access"),
                    CardBenefits("2 free young subscription"),
                    CardBenefits("2 free household subscription"),
                    CardBenefits("Priority customer support"),
                    CardBenefits("Travel insurance"),
                    CardBenefits("Mastercard World benefits", isLast = true))
            }
            else -> arrayListOf()
        }

    }
}