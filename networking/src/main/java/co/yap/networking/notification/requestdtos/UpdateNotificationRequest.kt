package co.yap.networking.notification.requestdtos

import com.google.gson.annotations.SerializedName

data class UpdateNotificationRequest(
    @SerializedName("notificationId")
    val notificationId: String? = null,
    @SerializedName("isRead")
    val isRead: Boolean? = null
)
