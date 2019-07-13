package com.hyperdev.tungguin.model.searchproduct

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SearchProductData (

    @SerializedName("current_page")
    @Expose
    var currentPage: Int? = null,
    @SerializedName("data")
    @Expose
    var dataProduct: List<SearchItem>? = null,
    @SerializedName("first_page_url")
    @Expose
    var firstPageUrl: String? = null,
    @SerializedName("from")
    @Expose
    var from: Int? = null,
    @SerializedName("last_page")
    @Expose
    var lastPage: Int? = null,
    @SerializedName("last_page_url")
    @Expose
    var lastPageUrl: String? = null,
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
    var to: Int? = null,
    @SerializedName("total")
    @Expose
    var total: Int? = null
)