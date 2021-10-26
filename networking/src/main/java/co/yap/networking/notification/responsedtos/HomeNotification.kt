package co.yap.networking.notification.responsedtos

import android.os.Parcelable
import co.yap.networking.models.ApiResponse
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HomeNotification(
    @SerializedName("notificationId")
    val id: String = "",
    @SerializedName("notificationType")
    var notificationType: String? = null,
    @SerializedName("title")
    var title: String? = null,
    @SerializedName("profilePicUrl")
    var profilePicUrl: String? = null,
    @SerializedName("firstName")
    var firstName: String? = null,
    @SerializedName("lastName")
    var lastName: String? = null,
    @SerializedName("currency")
    var currency: String? = null,
    @SerializedName("amount")
    var amount: String? = null,
    @SerializedName("transactionDate")
    var createdAt: String? = null,
    @SerializedName("isRead")
    var isRead: Boolean? = false,
    @SerializedName("isDeletable")
    var isDeletable: Boolean? = false,
    @SerializedName("notificationTxt")
    val description: String? = "",
    @SerializedName("action")
    val action: NotificationAction,
    @SerializedName("subtitle")
    val subTitle: String? = "",
    @SerializedName("imgResId")
    val imgResId: Int? = null,
    @Transient var isPinned: Boolean? = false,
    @Transient var btnTitle: String? = ""
) : ApiResponse(), Parcelable {
    fun fullName() = when {
        "$firstName $lastName" == "null null" -> {
            title
        }
        title == null -> {
            description
        }
        else -> "$firstName $lastName"
    }
}