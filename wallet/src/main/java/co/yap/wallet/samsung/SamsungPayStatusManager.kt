package co.yap.wallet.samsung

import android.content.Context
import android.os.Bundle
import co.yap.yapcore.helpers.SingletonHolder
import com.samsung.android.sdk.samsungpay.v2.SamsungPay
import com.samsung.android.sdk.samsungpay.v2.StatusListener
// Don not make is false it will always be true becasue samsung is live now 
fun Context.isSamsungPayFeatureEnabled() = true
class SamsungPayStatusManager private constructor(context: Context) {
    private var mSamsungPay: SamsungPay? = SamsungPayWalletManager.getInstance(context).mSamsungPay

    companion object : SingletonHolder<SamsungPayStatusManager, Context>(::SamsungPayStatusManager)


    fun getSamsungPayStatus(sPayStatus: ((SamsungPayStatus?) -> Unit)?) {
        mSamsungPay?.getSamsungPayStatus(object : StatusListener {
            override fun onSuccess(status: Int, bundle: Bundle?) {
                when (status) {
                    SamsungPay.SPAY_NOT_READY -> {
                        val extraReason = bundle?.getInt(SamsungPay.EXTRA_ERROR_REASON)
                        when (extraReason) {
                            SamsungPay.ERROR_SPAY_APP_NEED_TO_UPDATE -> sPayStatus?.invoke(
                                SamsungPayStatus.ERROR_SPAY_APP_NEED_TO_UPDATE
                            )
                            SamsungPay.ERROR_SPAY_SETUP_NOT_COMPLETED -> sPayStatus?.invoke(
                                SamsungPayStatus.ERROR_SPAY_SETUP_NOT_COMPLETE
                            )
                            SamsungPay.ERROR_PARTNER_SDK_API_LEVEL -> sPayStatus?.invoke(
                                SamsungPayStatus.ERROR_PARTNER_SDK_API_LEVEL
                            )
                            SamsungPay.ERROR_PARTNER_SERVICE_TYPE -> sPayStatus?.invoke(
                                SamsungPayStatus.ERROR_PARTNER_SERVICE_TYPE
                            )
                            else -> sPayStatus?.invoke(SamsungPayStatus.SPAY_NOT_SUPPORTED)
                        }
                    }
                    SamsungPay.SPAY_READY -> sPayStatus?.invoke(SamsungPayStatus.SPAY_READY)
                    SamsungPay.SPAY_NOT_SUPPORTED -> sPayStatus?.invoke(SamsungPayStatus.SPAY_NOT_SUPPORTED)
                }
            }

            override fun onFail(status: Int, bundle: Bundle?) {
                sPayStatus?.invoke(SamsungPayStatus.SPAY_NOT_SUPPORTED)
            }
        })
    }

    fun activateSamsungPay() {
        mSamsungPay?.activateSamsungPay()
    }

    fun updateSamsungPay() {
        mSamsungPay?.goToUpdatePage()
    }
}