package co.yap.networking.customers.responsedtos

import co.yap.networking.models.ApiResponse
import com.google.gson.annotations.SerializedName

data class CustomerNotificationResponse(
    @SerializedName("notificationId")
    var notificationId: String? = null,
    @SerializedName("notificationType")
    var notificationType: String? = null,
    @SerializedName("title")
    var title: String? = null,
    @SerializedName("notificationTxt")
    var notificationTxt: String? = null,
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
    var transactionDate: String? = null,
    @SerializedName("isRead")
    var isRead: String? = null,
    @SerializedName("isDeletable")
    var isDeletable: String? = null
) : ApiResponse()