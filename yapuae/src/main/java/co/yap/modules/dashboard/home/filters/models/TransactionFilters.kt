package co.yap.modules.dashboard.home.filters.models

import android.os.Parcelable
import co.yap.yapcore.constants.Constants.MANUAL_CREDIT
import co.yap.yapcore.constants.Constants.MANUAL_DEBIT
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TransactionFilters(
    var amountStartRange: Double? = null,
    var amountEndRange: Double? = null,
    var incomingTxn: Boolean? = false,
    var outgoingTxn: Boolean? = false,
    var pendingTxn: Boolean? = false,
    var totalAppliedFilter: Int = 0,
    var categories: ArrayList<String>? = arrayListOf()

) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (other is TransactionFilters) {
            return other.amountStartRange == amountStartRange
                    && other.amountEndRange == amountEndRange
                    && other.incomingTxn == incomingTxn
                    && other.outgoingTxn == outgoingTxn
                    && other.categories == categories
                    && other.pendingTxn == pendingTxn
        }
        return false
    }

    fun getTxnType(): String? {
        return if (incomingTxn == false && outgoingTxn == false && pendingTxn == false || incomingTxn == true && outgoingTxn == true && pendingTxn == true) {
            null
        } else if (incomingTxn == true)
            MANUAL_CREDIT
        else
            MANUAL_DEBIT
    }

    override fun hashCode(): Int {
        var result = amountStartRange?.hashCode() ?: 0
        result = 31 * result + (amountEndRange?.hashCode() ?: 0)
        result = 31 * result + (incomingTxn?.hashCode() ?: 0)
        result = 31 * result + (outgoingTxn?.hashCode() ?: 0)
        result = 31 * result + (pendingTxn?.hashCode() ?: 0)
        result = 31 * result + totalAppliedFilter
        result = 31 * result + (categories?.hashCode() ?: 0)
        return result
    }
}
