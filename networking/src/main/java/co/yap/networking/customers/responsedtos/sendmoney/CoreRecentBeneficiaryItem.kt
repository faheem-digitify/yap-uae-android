package co.yap.networking.customers.responsedtos.sendmoney

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
open class CoreRecentBeneficiaryItem(
    var name: String? = null,
    var profilePictureUrl: String? = "",
    var type: String? = "",
    var isoCountryCode: String? = ""
) : Parcelable