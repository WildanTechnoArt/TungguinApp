package com.hyperdev.tungguin.model.detailproduct

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.hyperdev.tungguin.network.ErrorResponse

data class DetailProductResponse (

    @SerializedName("meta")
    @Expose
    var meta: ErrorResponse? = null,
    @SerializedName("data")
    @Expose
    var data: ProductDetailItem? = null

)