package co.yap.modules.dashboard.store.cardplans.viewmodels

import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import co.yap.yapuae.R
import co.yap.modules.dashboard.store.cardplans.CardPlans
import co.yap.yapcore.BaseListItemViewModel

class CardPlansItemViewModel : BaseListItemViewModel<CardPlans>() {
    private lateinit var cardPlan: CardPlans
    override fun setItem(item: CardPlans, position: Int) {
        cardPlan = item
        notifyChange()
    }

    override fun getItem(): CardPlans = cardPlan

    override fun onFirsTimeUiCreate(bundle: Bundle?, navigation: NavController?) {
    }

    override fun layoutRes(): Int = R.layout.item_card_plans


    override fun onItemClick(view: View, data: Any, pos: Int) {
    }

}