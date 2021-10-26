package co.yap.modules.dashboard.cards.paymentcarddetail.activities.carddetaildialog

import android.view.View
import android.widget.Toast
import co.yap.yapcore.helpers.Utils.copyToClipboard
import co.yap.yapcore.helpers.extentions.toast

class CardDetailsDialogItemViewModel(
    var position: Int?,
    var cardDetailsModel: CardDetailsModel
) {
    fun onViewClicked(view: View) {
        copyToClipboard(view.context, cardDetailsModel.cardNumber ?: "")
        view.context.toast("Copied to clipboard", Toast.LENGTH_SHORT)
    }
}