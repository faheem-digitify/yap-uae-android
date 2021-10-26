package co.yap.networking.customers.responsedtos

import co.yap.networking.models.ApiResponse
import com.google.gson.annotations.SerializedName

data class QRContactResponse(
    @SerializedName("data")
    var qrContact: QRContact? = null,
    @SerializedName("errors")
    var errors: Any? = null
): ApiResponse()