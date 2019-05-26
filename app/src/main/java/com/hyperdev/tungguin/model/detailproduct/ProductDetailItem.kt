package com.hyperdev.tungguin.model.detailproduct

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ProductDetailItem (

    @SerializedName("name")
    @Expose
    var name: String? = null,
    @SerializedName("description")
    @Expose
    var description: String? = null,
    @SerializedName("hashed_id")
    @Expose
    var hashedId: String? = null,
    @SerializedName("icon_url")
    @Expose
    var iconUrl: String? = null,
    @SerializedName("formatted_price")
    @Expose
    var formattedPrice: String? = null,
    @SerializedName("price_list")
    @Expose
    var priceList: List<PriceList>? = null,
    @SerializedName("field_list_formatted")
    @Expose
    var fieldListFormatted: List<FieldListFormatted>? = null

)