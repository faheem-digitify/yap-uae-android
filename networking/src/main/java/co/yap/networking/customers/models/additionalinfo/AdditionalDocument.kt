package co.yap.networking.customers.models.additionalinfo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdditionalDocument(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("documentName")
    val name: String? = null,
    @SerializedName("uploadStatus")
    var isUploaded: Boolean? = null,
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
    @SerializedName("documentType")
    val documentType: String? = null,
    @SerializedName("archive")
    val archive: Boolean? = null,
    @SerializedName("questionOnly")
    val questionOnly: Boolean? = null,
    @SerializedName("status")
    var status: String? = null
) : Parcelable