package co.yap.widgets.bottomsheet

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BottomSheetItem(
    var icon: Int? = -1,
    var title: String? = "",
    var subTitle: String? = "",
    var tag: String? = ""
) : Parcelable