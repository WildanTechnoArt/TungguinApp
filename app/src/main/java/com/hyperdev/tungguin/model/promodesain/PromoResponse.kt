package com.hyperdev.tungguin.model.promodesain

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.hyperdev.tungguin.network.ErrorResponse

data class PromoResponse (

    @SerializedName("meta")
    @Expose
    var meta: ErrorResponse? = null,

    @SerializedName("data")
    @Expose
    var data: List<PromoData>? = null

)