package co.yap.modules.dashboard.store.cardplans.viewmodels

import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import co.yap.yapuae.R
import co.yap.modules.dashboard.store.cardplans.CardBenefits
import co.yap.yapcore.BaseListItemViewModel

class PlanBenefitsItemViewModel : BaseListItemViewModel<CardBenefits>() {
    private lateinit var cardBenefits: CardBenefits
    override fun setItem(item: CardBenefits, position: Int) {
        cardBenefits = item
        notifyChange()
    }

    override fun getItem(): CardBenefits = cardBenefits

    override fun onFirsTimeUiCreate(bundle: Bundle?, navigation: NavController?) {
    }

    override fun layoutRes(): Int = R.layout.item_card_benefits


    override fun onItemClick(view: View, data: Any, pos: Int) {
    }

}