package co.yap.networking.customers.requestdtos

import co.yap.networking.models.ApiResponse
import com.google.gson.annotations.SerializedName

data class MsCustomerNotificationsRequest(
    @SerializedName("token")
    val token: String? = null,
    @SerializedName("device_id")
    val device_id: String? = null
): ApiResponse()