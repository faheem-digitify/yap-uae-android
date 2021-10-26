package co.yap.wallet.samsung

import android.content.Context
import androidx.core.os.bundleOf
import co.yap.app.YAPApplication
import co.yap.yapcore.helpers.SingletonHolder
import com.samsung.android.sdk.samsungpay.v2.PartnerInfo
import com.samsung.android.sdk.samsungpay.v2.SamsungPay
import com.samsung.android.sdk.samsungpay.v2.SpaySdk

class PartnerInfoHolder private constructor(private val context: Context) {
    val partnerInfo: PartnerInfo

    init {
        val serviceId = YAPApplication.configManager?.spayServiceId
        partnerInfo = PartnerInfo(
            serviceId,
            bundleOf(
                SamsungPay.PARTNER_SERVICE_TYPE to SpaySdk.ServiceType.APP2APP.toString(),
                SamsungPay.EXTRA_ISSUER_NAME to "YAP PAYMENT SERVICES PROVIDER LLC"
            )
        )
    }

    companion object : SingletonHolder<PartnerInfoHolder, Context>(::PartnerInfoHolder)
}