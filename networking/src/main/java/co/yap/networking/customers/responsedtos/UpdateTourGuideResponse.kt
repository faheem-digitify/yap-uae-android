package co.yap.networking.customers.responsedtos


import android.os.Parcelable
import co.yap.networking.models.ApiResponse
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UpdateTourGuideResponse(
    @SerializedName("errors")
    val errors: String? = null,
    @SerializedName("data")
    val data: TourGuide? = null
) : Parcelable, ApiResponse() {
}