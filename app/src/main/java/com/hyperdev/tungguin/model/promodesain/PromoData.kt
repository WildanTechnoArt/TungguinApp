package com.hyperdev.tungguin.model.promodesain

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PromoData (

    @SerializedName("label")
    @Expose
    var label: String? = null,

    @SerializedName("items")
    @Expose
    var items: List<PromoItem>? = null

)