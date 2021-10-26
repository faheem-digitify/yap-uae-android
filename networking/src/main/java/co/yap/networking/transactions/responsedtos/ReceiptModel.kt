package co.yap.networking.transactions.responsedtos

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ReceiptModel(
    val title: String,
    val receiptImageUrl: String? = null
) : Parcelable {
    val receiptId: String get() = receiptImageUrl?.split("/")?.last() ?: ""
}
