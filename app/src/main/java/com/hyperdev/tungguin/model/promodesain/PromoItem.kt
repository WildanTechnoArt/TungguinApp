package com.hyperdev.tungguin.model.promodesain

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PromoItem (

    @SerializedName("name")
    @Expose
    var name: String? = null,
    @SerializedName("hashed_id")
    @Expose
    var hashedId: String? = null,
    @SerializedName("icon_url")
    @Expose
    var iconUrl: String? = null,
    @SerializedName("formatted_price")
    @Expose
    var formattedPrice: String? = null

)