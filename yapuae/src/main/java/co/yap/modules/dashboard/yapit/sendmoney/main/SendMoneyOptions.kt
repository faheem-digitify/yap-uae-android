package co.yap.modules.dashboard.yapit.sendmoney.main

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class SendMoneyOptions(
    var name: String,
    val image: Int,
    val showFlag: Boolean,
    var flag: Int? = null,
    var type: SendMoneyType = SendMoneyType.none
) : Parcelable

enum class SendMoneyType {
    sendMoneyToYAPContacts, sendMoneyToLocalBank, sendMoneyToHomeCountry, sendMoneyQRCode, sendMoneyToInternational, none
}