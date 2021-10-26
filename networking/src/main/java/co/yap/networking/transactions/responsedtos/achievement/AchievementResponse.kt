package co.yap.networking.transactions.responsedtos.achievement

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AchievementResponse(
    @SerializedName("achievementType")
    val achievementType: String? = null,
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("order")
    val order: Int? = null,
    @SerializedName("colorCode")
    val color: String? = null,
    @SerializedName("percentage")
    val percentage: Double? = null,
    @SerializedName("lock")
    val isForceLocked: Boolean? = null,
    @SerializedName("tasks")
    val goals: List<AchievementTask>? = null,
    @SerializedName("lastUpdated")
    val lastUpdated: String? = null
) : Parcelable {
    val isCompleted: Boolean get() = percentage == 100.00
}