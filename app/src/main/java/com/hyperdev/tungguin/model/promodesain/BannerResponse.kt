package com.hyperdev.tungguin.model.promodesain

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.hyperdev.tungguin.network.ErrorResponse

data class BannerResponse (

    @SerializedName("meta")
    @Expose
    var meta: ErrorResponse? = null,

    @SerializedName("data")
    @Expose
    var data: List<BannerData>? = null

)