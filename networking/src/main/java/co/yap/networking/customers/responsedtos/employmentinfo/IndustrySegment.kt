package co.yap.networking.customers.responsedtos.employmentinfo

import android.os.Parcelable
import co.yap.networking.coreitems.CoreBottomSheetData
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class IndustrySegment(
    @SerializedName("subSegmentCode")
    val segmentCode: String?,
    @SerializedName("subSegmentDesc")
    val segment: String?
) : Parcelable, CoreBottomSheetData()