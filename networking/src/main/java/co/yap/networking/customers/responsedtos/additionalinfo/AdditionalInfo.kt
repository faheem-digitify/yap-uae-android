package co.yap.networking.customers.responsedtos.additionalinfo

import android.os.Parcelable
import co.yap.networking.customers.models.additionalinfo.AdditionalDocument
import co.yap.networking.customers.models.additionalinfo.AdditionalQuestion
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdditionalInfo(
    @SerializedName("documentInfo")
    var documentInfo: List<AdditionalDocument>? = null,
    @SerializedName("textInfo")
    var textInfo: List<AdditionalQuestion>? = null
) : Parcelable