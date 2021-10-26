package co.yap.networking.transactions.responsedtos.transaction

import com.google.gson.annotations.SerializedName


data class Pageable(
    @SerializedName("offset")
    var offset: Int?=0,
    @SerializedName("pageNumber")
    var pageNumber: Int?=0,
    @SerializedName("pageSize")
    var pageSize: Int?=0,
    @SerializedName("paged")
    var paged: Boolean?=true,
    @SerializedName("sort")
    var sort: Sort?=Sort(),
    @SerializedName("unpaged")
    var unpaged: Boolean?=false
)