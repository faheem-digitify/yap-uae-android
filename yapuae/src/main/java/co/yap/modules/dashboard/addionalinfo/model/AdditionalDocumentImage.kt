package co.yap.modules.dashboard.addionalinfo.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.File

@Parcelize
data class AdditionalDocumentImage(
    val file: File,
    var name: String
) : Parcelable