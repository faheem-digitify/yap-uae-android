package co.yap.modules.dashboard.cards.paymentcarddetail.activities.carddetaildialog

import androidx.recyclerview.widget.RecyclerView
import co.yap.yapuae.databinding.DialogCardDetailsCardSerialNumberBinding
import co.yap.yapcore.helpers.extentions.loadCardImage
import co.yap.yapcore.helpers.extentions.toCamelCase
import co.yap.yapcore.interfaces.OnItemClickListener

class CardDetailsSerialNumberViewHolder(private val dialogCardDetailsCardSerialNumberBinding: DialogCardDetailsCardSerialNumberBinding) :
    RecyclerView.ViewHolder(dialogCardDetailsCardSerialNumberBinding.root) {
    fun onBind(
        cardDetailsModel: CardDetailsModel,
        position: Int
        ) {
        dialogCardDetailsCardSerialNumberBinding.ivCard.loadCardImage(cardDetailsModel.cardImg)
        dialogCardDetailsCardSerialNumberBinding.tvCardType.text = cardDetailsModel.cardType
        dialogCardDetailsCardSerialNumberBinding.tvCardNumberValue.text =
            cardDetailsModel.cardNumber?.toCamelCase()
        dialogCardDetailsCardSerialNumberBinding.tvCardName.text =
            cardDetailsModel.displayName
        dialogCardDetailsCardSerialNumberBinding.viewModel?.position = position
        dialogCardDetailsCardSerialNumberBinding.viewModel =
            CardDetailsDialogItemViewModel(
                position,
                cardDetailsModel
            )
        dialogCardDetailsCardSerialNumberBinding.executePendingBindings()
    }
}