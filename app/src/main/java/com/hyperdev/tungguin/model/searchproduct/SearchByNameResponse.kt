package com.hyperdev.tungguin.model.searchproduct

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.hyperdev.tungguin.network.ErrorResponse

data class SearchByNameResponse (
    @SerializedName("meta")
    @Expose
    var meta: ErrorResponse? = null,
    @SerializedName("data")
    @Expose
    var data: SearchProductData? = null
)
