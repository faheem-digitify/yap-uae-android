package co.yap.modules.dashboard.store.cardplans.viewmodels

import android.app.Application
import co.yap.modules.dashboard.store.cardplans.interfaces.ICardViewer
import co.yap.modules.dashboard.store.cardplans.states.CardViewerState
import co.yap.yapcore.constants.Constants

class CardPlanViewerViewModel(application: Application) :
    CardPlansBaseViewModel<ICardViewer.State>(application), ICardViewer.ViewModel {
    override val state: ICardViewer.State = CardViewerState()

    override fun getFragmentToDisplay(id: String?): Int {
        return when (id) {
            Constants.PRIME_CARD_PLAN -> 0
            Constants.METAL_CARD_PLAN -> 1
            else -> 3
        }
    }
}