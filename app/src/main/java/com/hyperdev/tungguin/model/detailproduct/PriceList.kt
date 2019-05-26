package com.hyperdev.tungguin.model.detailproduct

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PriceList (

    @SerializedName("design_option_count")
    @Expose
    var designOptionCount: Int? = null,
    @SerializedName("price")
    @Expose
    var price: Int? = null,
    @SerializedName("price_formatted")
    @Expose
    var priceFormatted: String? = null

)