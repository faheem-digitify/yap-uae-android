package co.yap.modules.dashboard.cards.paymentcarddetail.activities.carddetaildialog

import androidx.recyclerview.widget.RecyclerView
import co.yap.yapuae.databinding.DialogCardDetailsCardExpiryBinding
import co.yap.yapcore.helpers.extentions.loadCardImage
import co.yap.yapcore.interfaces.OnItemClickListener

class CardDetailsExpiryViewHolder(private val dialogCardDetailsCardExpiryBinding: DialogCardDetailsCardExpiryBinding) :
    RecyclerView.ViewHolder(dialogCardDetailsCardExpiryBinding.root) {
    fun onBind(
        cardDetailsModel: CardDetailsModel,
        position: Int
    ) {
        dialogCardDetailsCardExpiryBinding.ivCard.loadCardImage(cardDetailsModel.cardImg)
        dialogCardDetailsCardExpiryBinding.tvCardType.text = cardDetailsModel.cardType
        dialogCardDetailsCardExpiryBinding.tvCardValidityValue.text =
            cardDetailsModel.cardExpiry
        dialogCardDetailsCardExpiryBinding.tvCvvValue.text =
            cardDetailsModel.cardCvv
        dialogCardDetailsCardExpiryBinding.tvCardName.text =
            cardDetailsModel.displayName
        dialogCardDetailsCardExpiryBinding.viewModel?.position = position
        dialogCardDetailsCardExpiryBinding.viewModel =
            CardDetailsDialogItemViewModel(
                position,
                cardDetailsModel
            )
        dialogCardDetailsCardExpiryBinding.executePendingBindings()
    }
}