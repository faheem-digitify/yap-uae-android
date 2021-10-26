package co.yap.networking.customers.models.additionalinfo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdditionalQuestion(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("uploadStatus")
    val isUploaded: Boolean? = null,
    @SerializedName("creationDate")
    val creationDate: Boolean? = null,
    @SerializedName("createdBy")
    val createdBy: String? = null,
    @SerializedName("updatedDate")
    val updatedDate: String? = null,
    @SerializedName("updatedBy")
    val updatedBy: String? = null,
    @SerializedName("accountUUID")
    val accountUUID: String? = null,
    @SerializedName("questionFromCustomer")
    val questionFromCustomer: String? = null,
    @SerializedName("archive")
    val archive: Boolean? = null,
    @SerializedName("questionOnly")
    val questionOnly: Boolean? = null,
    @SerializedName("status")
    val status: String? = null
) : Parcelable