package com.hyperdev.tungguin.model.transactionhistory

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class TransactionHistory (
    @SerializedName("current_page")
    @Expose
    var currentPage: Int? = null,
    @SerializedName("data")
    @Expose
    var dataTransaction: List<ListTransaction>? = null,
    @SerializedName("first_page_url")
    @Expose
    var firstPageUrl: String? = null,
    @SerializedName("from")
    @Expose
    var from: Any? = null,
    @SerializedName("next_page_url")
    @Expose
    var nextPageUrl: Any? = null,
    @SerializedName("path")
    @Expose
    var path: String? = null,
    @SerializedName("per_page")
    @Expose
    var perPage: Int? = null,
    @SerializedName("prev_page_url")
    @Expose
    var prevPageUrl: Any? = null,
    @SerializedName("to")
    @Expose
    var to: Any? = null
)