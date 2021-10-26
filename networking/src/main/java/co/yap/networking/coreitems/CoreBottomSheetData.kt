package co.yap.networking.coreitems

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
open class CoreBottomSheetData(
    var sheetImage: Int? = -1,
    var content: String? = "",
    var subTitle: String? = "",
    var isSelected: Boolean? = false
) : Parcelable
