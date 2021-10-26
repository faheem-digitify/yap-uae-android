package co.yap.networking.transactions.responsedtos.transaction

import co.yap.networking.models.ApiResponse
import com.google.gson.annotations.SerializedName


data class HomeTransactionListData(
    @SerializedName("type")
    var type: String?=null,
    @SerializedName("totalAmountType")
    var totalAmountType: String?=null,
    @SerializedName("date")
    var date: String?,
    @SerializedName("totalAmount")
    var totalAmount: String?="",
    @SerializedName("closingBalance")
    var closingBalance: Double?=0.0,
    @SerializedName("amountPercentage")
    var amountPercentage: Double?=0.0,
    @SerializedName("content")
    var transaction: List<Transaction>,
    @SerializedName("first")
    var first: Boolean?=false,
    @SerializedName("last")
    var last: Boolean?=false,
    @SerializedName("number")
    var number: Int?=0,
    @SerializedName("numberOfElements")
    var numberOfElements: Int?=0,
    @SerializedName("pageable")
    var pageable: Pageable?= Pageable(),
    @SerializedName("size")
    var size: Int?=0,
    @SerializedName("sort")
    var sort: Sort?=Sort(),
    @SerializedName("totalElements")
    var totalElements: Int?=0,
    @SerializedName("totalPages")
    var totalPages: Int?=0,
    @Transient
    var originalDate: String? = "",
    @Transient
    var isNewItem: Boolean = false

): ApiResponse()