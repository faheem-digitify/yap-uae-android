package co.yap.modules.dashboard.store.cardplans.viewmodels

import android.app.Application
import co.yap.modules.dashboard.store.cardplans.CardPlans
import co.yap.yapcore.BaseViewModel
import co.yap.yapcore.IBase

abstract class CardPlansBaseViewModel<S : IBase.State>(application: Application) :
    BaseViewModel<S>(application) {
    var parentViewModel: CardPlansMainViewModel? = null

    fun getCards() :MutableList<CardPlans>? = parentViewModel?.cards

}
