package co.yap.networking.notification.responsedtos
import co.yap.networking.models.ApiResponse
import com.google.gson.annotations.SerializedName


data class FcmNotificationCount(
    @SerializedName("data") var data: String? = null
):ApiResponse()