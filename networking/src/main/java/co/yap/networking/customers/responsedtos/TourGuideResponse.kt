package co.yap.networking.customers.responsedtos

import android.os.Parcelable
import co.yap.networking.models.ApiResponse
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TourGuideResponse(
    @SerializedName("errors")
    val errors: String? = null,
    @SerializedName("data")
    val data: ArrayList<TourGuide>? = arrayListOf()
) : Parcelable, ApiResponse() {
}