package co.yap.modules.dashboard.cards.home.viewmodels

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import co.yap.modules.others.helper.Constants
import co.yap.networking.cards.responsedtos.Card
import co.yap.wallet.samsung.SamsungPayStatus
import co.yap.wallet.samsung.SamsungPayStatusManager
import co.yap.wallet.samsung.isSamsungPayFeatureEnabled
import co.yap.yapcore.BR
import co.yap.yapcore.enums.CardPinStatus
import co.yap.yapcore.enums.CardStatus
import co.yap.yapcore.enums.CardType
import co.yap.yapcore.enums.PartnerBankStatus
import co.yap.yapcore.helpers.confirm
import co.yap.yapcore.interfaces.OnItemClickListener
import co.yap.yapcore.managers.SessionManager

class YapCardItemViewModel(
    private val context: Context,
    val paymentCard: Card?,
    val position: Int,
    private val onItemClickListener: OnItemClickListener?
) : BaseObservable() {
    //    var sPayStatus: MutableLiveData<SamsungPayStatus>? =
//        MutableLiveData(SamsungPayStatus.SPAY_NOT_SUPPORTED)
    @get:Bindable
    var sPayStatus: SamsungPayStatus? = SamsungPayStatus.SPAY_NOT_SUPPORTED
        set(value) {
            field = value
            notifyPropertyChanged(BR.sPayStatus)
        }

    @get:Bindable
    var cardName: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.sPayStatus)
        }

    init {
        getCardName()
        if (context.isSamsungPayFeatureEnabled()) {
            paymentCard?.let {
                if (isCardActive(card = paymentCard)) {
                    SamsungPayStatusManager.getInstance(context).getSamsungPayStatus {
                        sPayStatus = it
                    }
                }
            }
        }
    }

    // Custom logic if there any and add only observable or mutable dataList if your really need it.
    // You can also add methods for callbacks from xml
    fun handlePressOnView(view: View) {
        onItemClickListener?.onItemClick(view, paymentCard!!, position)
    }

    fun handlePressOnSamsung(view: View) {
//        SamsungPayWalletManager.getInstance(context).getWalletInfo { status, cards ->
//            val clientDeviceId = status
//            Log.d("", "")
//        }
//        return
        SamsungPayStatusManager.getInstance(context).getSamsungPayStatus {
            when (it) {
                SamsungPayStatus.SPAY_READY -> {
                    paymentCard?.let { onItemClickListener?.onItemClick(view, it, position) }
                }
                SamsungPayStatus.ERROR_SPAY_SETUP_NOT_COMPLETE -> {
                    if (context is Activity)
                        context.confirm(
                            message = "Samsung Pay app needs to be set up. Activate the app to add a card to Samsung Pay app",
                            title = "Activate Samsung Pay",
                            positiveButton = "Continue"
                        ) {
                            SamsungPayStatusManager.getInstance(
                                context
                            ).activateSamsungPay()
                        }
                }
                SamsungPayStatus.ERROR_SPAY_APP_NEED_TO_UPDATE -> {
                    if (context is Activity)
                        context.confirm(
                            message = "A new version of Damsung Pay is available. Update the app to add a card to Samsung Pay app",
                            title = "Update Samsung Pay",
                            positiveButton = "Continue"
                        ) {
                            SamsungPayStatusManager.getInstance(
                                context
                            ).updateSamsungPay()
                        }
                }
            }
        }
    }

    private fun isCardActive(card: Card): Boolean {
        var status = false
        if (CardStatus.valueOf(card.status).name.isNotEmpty())
            status = when (CardStatus.valueOf(card.status)) {
                CardStatus.ACTIVE -> {
                    if (card.cardType == CardType.DEBIT.type) {
                        if (card.pinStatus == CardPinStatus.BLOCKED.name) false
                        else
                            !(PartnerBankStatus.ACTIVATED.status == SessionManager.user?.partnerBankStatus && !card.pinCreated)
                    } else
                        true
                }
                CardStatus.BLOCKED, CardStatus.HOTLISTED, CardStatus.INACTIVE, CardStatus.EXPIRED -> {
                    false
                }
            }
        return status
    }

    private fun getCardName() {
        if (Constants.CARD_TYPE_DEBIT == paymentCard?.cardType)
            cardName = Constants.TEXT_PRIMARY_CARD
        else
            if (null != paymentCard?.nameUpdated) {
                if (paymentCard.nameUpdated!!) {
                    cardName = paymentCard.cardName ?: ""
                } else {
                    if (paymentCard.physical) {
                        cardName = Constants.TEXT_SPARE_CARD_PHYSICAL
                    } else {
                        cardName = paymentCard.cardName ?: ""
                    }
                }
            } else {
                if (paymentCard?.physical!!) {
                    cardName = Constants.TEXT_SPARE_CARD_PHYSICAL
                } else {
                    cardName = paymentCard.cardName ?: ""
                }

            }
    }
}