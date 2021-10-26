package co.yap.networking.transactions.requestdtos

import com.google.gson.annotations.SerializedName

const val REQUEST_PAGE_SIZE = 200

data class HomeTransactionsRequest(
    @SerializedName("number")
    var number: Int = 0,
    @SerializedName("size")
    var size: Int = 0,
    @SerializedName("amountStartRange")
    var amountStartRange: Double? = 0.0,
    @SerializedName("amountEndRange")
    var amountEndRange: Double? = 0.0,
    @SerializedName("txnType")
    var txnType: String? = null,
    @SerializedName("title")
    var title: String? = null,
    @SerializedName("totalAppliedFilter")
    var totalAppliedFilter: Int = 0,
    @SerializedName("searchField")
    var searchField: String? = null,
    @SerializedName("merchantCategoryNames")
    var categories: ArrayList<String>? = null,
    @SerializedName("statuses")
    var statues: ArrayList<String>? = null,
    @SerializedName("cardDetailsRequired")
    val cardDetailsRequired: Boolean = true
)
