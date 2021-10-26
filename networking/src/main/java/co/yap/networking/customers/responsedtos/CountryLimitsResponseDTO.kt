package co.yap.networking.customers.responsedtos

import co.yap.networking.models.ApiResponse
import com.google.gson.annotations.SerializedName

class CountryLimitsResponseDTO(
    @SerializedName("data")
    var data: String?
) : ApiResponse() {
}