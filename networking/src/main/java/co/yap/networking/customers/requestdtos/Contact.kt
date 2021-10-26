package co.yap.networking.customers.requestdtos

import android.os.Parcelable
import co.yap.networking.customers.responsedtos.sendmoney.IBeneficiary
import co.yap.networking.models.ApiResponse
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Contact(
    @SerializedName("title")
    var title: String? = "",
    @SerializedName("countryCode")
    var countryCode: String? = "",
    @SerializedName("mobileNo")
    var mobileNo: String? = "",
    @SerializedName("email")
    var email: String? = "",
    @SerializedName("beneficiaryPictureUrl")
    var beneficiaryPictureUrl: String? = "",
    @SerializedName("yapUser")
    var yapUser: Boolean? = false,
    @SerializedName("accountDetailList")
    val accountDetailList: List<Data>? = null,
    @SerializedName("beneficiaryCreationDate")
    var beneficiaryCreationDate: String? = ""
) : ApiResponse(), IBeneficiary, Parcelable {

    @IgnoredOnParcel
    override val fullName: String?
        get() = title

    @IgnoredOnParcel
    override val subtitle: String?
        get() = mobileNo

    @IgnoredOnParcel
    override val userType: String?
        get() = if (yapUser == true) "Y2Y" else ""

    override val imgUrl: String?
        get() = beneficiaryPictureUrl

    override val isYapUser: Boolean
        get() = yapUser == true

    override val accountUUID: String
        get() = accountDetailList?.get(0)?.accountUuid ?: ""

    override val creationDateOfBeneficiary: String
        get() = beneficiaryCreationDate ?: ""

    @Parcelize
    data class Data(
        @SerializedName("accountNo")
        val accountNo: String? = "",
        @SerializedName("accountType")
        val accountType: String? = "",
        @SerializedName("accountUuid")
        val accountUuid: String? = ""
    ) : ApiResponse(), Parcelable
}