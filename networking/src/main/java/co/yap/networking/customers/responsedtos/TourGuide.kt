package co.yap.networking.customers.responsedtos

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class TourGuide (
    @SerializedName("viewName")
    val viewName: String? = null,
    @SerializedName("accountUuid")
    val accountUuid: String? = null,
    @SerializedName("skipped")
    val skipped: Boolean? = null,
    @SerializedName("completed")
    val completed: Boolean? = null,
    @SerializedName("completedAt")
    val completedAt: String? = null
) : Parcelable