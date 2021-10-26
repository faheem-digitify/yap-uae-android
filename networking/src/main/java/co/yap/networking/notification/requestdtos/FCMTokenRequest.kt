package co.yap.networking.notification.requestdtos

import android.os.Build
import co.yap.networking.models.ApiResponse
import com.google.gson.annotations.SerializedName

data class FCMTokenRequest(
    @SerializedName("token")
    val token: String? = null,
    @SerializedName("deviceId")
    val deviceId: String? = null,
    @SerializedName("deviceName")
    val deviceName: String? = Build.BRAND,
    @SerializedName("osType")
    val osType: String? = "Android"
) : ApiResponse()