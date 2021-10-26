package co.yap.wallet.samsung

import android.content.Context
import android.os.Bundle
import androidx.core.os.bundleOf
import co.yap.wallet.R
import co.yap.widgets.State
import co.yap.yapcore.helpers.SingletonHolder
import com.samsung.android.sdk.samsungpay.v2.AppToAppConstants.ERROR_FRAMEWORK_INTERNAL
import com.samsung.android.sdk.samsungpay.v2.AppToAppConstants.ERROR_INVALID_CARD
import com.samsung.android.sdk.samsungpay.v2.SamsungPay
import com.samsung.android.sdk.samsungpay.v2.SpaySdk.*
import com.samsung.android.sdk.samsungpay.v2.StatusListener
import com.samsung.android.sdk.samsungpay.v2.card.*
import com.samsung.android.sdk.samsungpay.v2.payment.PaymentManager

class SamsungPayWalletManager private constructor(private val context: Context) {
    companion object : SingletonHolder<SamsungPayWalletManager, Context>(::SamsungPayWalletManager)

    internal var mSamsungPay: SamsungPay? =
        SamsungPay(context, PartnerInfoHolder.getInstance(context).partnerInfo)
    private var mPaymentManager: PaymentManager? =
        PaymentManager(context, PartnerInfoHolder.getInstance(context).partnerInfo)
    private var mCardManager: CardManager? =
        CardManager(context, PartnerInfoHolder.getInstance(context).partnerInfo)

    fun getWalletInfo(response: (Int, Bundle?, State?) -> Unit) {
        val keys =
            mutableListOf(SamsungPay.WALLET_DM_ID, SamsungPay.DEVICE_ID, SamsungPay.WALLET_USER_ID)
        mSamsungPay?.getWalletInfo(keys, object : StatusListener {
            override fun onSuccess(status: Int, walletData: Bundle?) {
                walletData?.let {
                    if (status == SamsungPay.ERROR_NONE) {
                        val clientDeviceId = it.getString(SamsungPay.DEVICE_ID)
                        val clientWalletAccountId = it.getString(SamsungPay.WALLET_USER_ID)
                        response.invoke(status, walletData, State.success(""))
                    }
                }
            }

            override fun onFail(errorCode: Int, errorData: Bundle?) {
                response.invoke(
                    errorCode,
                    errorData,
                    State.error(ErrorCode.getInstance().getSPayError(errorCode, errorData))
                )
                // Check the extra error codes in the errorData bundle for all the reasons in
                // SamsungPay.EXTRA_ERROR_REASON, when provided
//                when (status) {
//                    SamsungPay.SPAY_NOT_READY ->
//                        context.alert("Samsung Pay is not completely activated. Open Samsung Pay app  signed in with a valid Samsung Account and activate.")
//                    SamsungPay.SPAY_NOT_ALLOWED_TEMPORALLY -> context.alert("Samsung Pay is not allowed temporally!")
//                }
            }
        })
    }

    fun getAllCards(response: (SamsungPayStatus, MutableList<Card>?, State?) -> Unit) {
        SamsungPayStatusManager.getInstance(context).getSamsungPayStatus {
            when (it) {
                SamsungPayStatus.SPAY_READY -> {
                    mCardManager?.getAllCards(
                        null,
                        object : GetCardListener {
                            override fun onSuccess(cardList: MutableList<Card>?) {
                                response.invoke(
                                    SamsungPayStatus.SPAY_READY,
                                    cardList,
                                    State.success("")
                                )
                            }

                            override fun onFail(errorCode: Int, errorData: Bundle?) {
                                response.invoke(
                                    SamsungPayStatus.SPAY_NOT_READY,
                                    null,
                                    State.error(
                                        ErrorCode.getInstance().getSPayError(errorCode, errorData)
                                    )
                                )
//                    when (errorCode) {
//                        SamsungPay.SPAY_NOT_READY -> {
//                            context.alert("Samsung Pay is not completely activated. Open Samsung Pay app  signed in with a valid Samsung Account and activate.")
//                            response.invoke(SamsungPayStatus.SPAY_NOT_READY, null)
//                        }
//                        SamsungPay.SPAY_NOT_ALLOWED_TEMPORALLY -> {
//                            context.alert("Samsung Pay is not allowed temporally!")
//                            response.invoke(SamsungPayStatus.SPAY_NOT_READY, null)
//                        }
//                    }
                            }
                        })
                }
                else -> response.invoke(
                    it ?: SamsungPayStatus.SPAY_NOT_READY,
                    null,
                    State.error("")
                )
            }
        }
    }

    fun addYapCardToSamsungPay(payload: String?, success: (State) -> Unit) {
        payload?.let {
            val mNetworkProvider: String = AddCardInfo.PROVIDER_MASTERCARD
            val cardDetail = bundleOf(AddCardInfo.EXTRA_PROVISION_PAYLOAD to it)
            val addCardInfo = AddCardInfo(
                Card.CARD_TYPE_DEBIT,
                mNetworkProvider,
                cardDetail
            )
            mCardManager?.addCard(addCardInfo, object : AddCardListener {
                override fun onSuccess(status: Int, p1: Card?) {
                    success.invoke(State.success("Card successfully added to Samsung Pay."))
                }

                override fun onFail(errorCode: Int, errorData: Bundle?) {
                    val errorMessage = when (errorCode) {
                        ERROR_NO_NETWORK -> context.getString(R.string.common_display_text_error_no_internet)
                        ERROR_SPAY_INTERNAL -> context.getString(R.string.screen_cards_display_text_error_spay_internal)
                        ERROR_FRAMEWORK_INTERNAL -> context.getString(R.string.screen_cards_display_text_error_framework_internal)
                        ERROR_INVALID_INPUT, ERROR_INVALID_CARD -> context.getString(R.string.screen_cards_display_text_error_invalid_card_input)
                        ERROR_USER_CANCELED -> context.getString(R.string.screen_cards_display_text_error_user_canceled)
                        else -> ErrorCode.getInstance().getSPayError(errorCode, errorData)
                    }
                    success.invoke(
                        State.error(errorMessage)
                    )
                }

                override fun onProgress(currentCount: Int, p1: Int, bundleData: Bundle?) {
                    //success.invoke(State.loading("Card adding in progress"))PENDING_ACTIVATION  DSAPMC0000153622bd92d7712a3240ceab97efcc5739aeb7
//                    context.alert(ErrorCode.getInstance().getSPayError(errorCode, errorData))
                }
            })
        }
    }

    fun openFavoriteCard(cardId: String?, success: (State) -> Unit) {
        val metaData =
            bundleOf(
                PaymentManager.EXTRA_ISSUER_NAME to "YAP PAYMENT SERVICES PROVIDER LLC",
                PaymentManager.EXTRA_PAY_OPERATION_TYPE to PaymentManager.PAY_OPERATION_TYPE_PAYMENT,
                PaymentManager.EXTRA_TRANSACTION_TYPE to PaymentManager.TRANSACTION_TYPE_MST
            )
        val cardInfo =
            com.samsung.android.sdk.samsungpay.v2.payment.CardInfo.Builder().setCardId(cardId)
                .setCardMetaData(metaData).build()
        mPaymentManager?.startSimplePay(cardInfo, object : StatusListener {
            override fun onSuccess(p0: Int, p1: Bundle?) {
                success.invoke(State.success(""))
            }

            override fun onFail(errorCode: Int, errorData: Bundle?) {
                success.invoke(
                    State.error(
                        ErrorCode.getInstance().getSPayError(errorCode, errorData)
                    )
                )
            }
        })
    }
}