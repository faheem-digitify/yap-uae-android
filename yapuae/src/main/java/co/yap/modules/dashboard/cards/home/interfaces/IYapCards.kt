package co.yap.modules.dashboard.cards.home.interfaces

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import co.yap.networking.cards.responsedtos.Card
import co.yap.networking.cards.responsedtos.CardDetail
import co.yap.yapcore.IBase
import co.yap.yapcore.SingleClickEvent

interface IYapCards {

    interface View : IBase.View<ViewModel>

    interface ViewModel : IBase.ViewModel<State> {
        val clickEvent: SingleClickEvent
        val cards: MutableLiveData<ArrayList<Card>>
        var selectedCardPosition: Int
        fun getCards()
        fun getUpdatedCard(cardPosition: Int, card: (Card?) -> Unit)
        fun updateCardCount(size: Int)
        fun unFreezeCard(cardSerialNumber: String, success: () -> Unit)
        fun getSamsungPayloadAndAddCard(
            cardSerialNumber: String,
            success: (String?, co.yap.widgets.State) -> Unit
        )

        fun getCardDetails(cardSerialNumber: String, success: (CardDetail?) -> Unit)
        fun openFavoriteCard(cardId: String?, success: (co.yap.widgets.State) -> Unit)
        fun removeCard(card: Card?)
    }

    interface State : IBase.State {
        var noOfCard: String
        var enableAddCard: ObservableBoolean
    }
}
