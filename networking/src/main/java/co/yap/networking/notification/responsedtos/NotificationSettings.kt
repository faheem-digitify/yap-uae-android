package co.yap.networking.notification.responsedtos
import android.os.Parcelable
import co.yap.networking.models.ApiResponse
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NotificationSettings(
    @SerializedName("emailEnabled") var emailEnabled: Boolean? = true,
    @SerializedName("inAppEnabled") var inAppEnabled: Boolean? = true,
    @SerializedName("smsEnabled") var smsEnabled: Boolean? = true
):ApiResponse(), Parcelable