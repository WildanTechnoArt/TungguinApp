package com.hyperdev.tungguin.model.cart

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AddCartData(

    @SerializedName("hashed_id")
    @Expose
    var hashedId: String? = null,
    @SerializedName("product_name")
    @Expose
    var productName: String? = null,
    @SerializedName("formatted_number_of_design")
    @Expose
    var formattedNumberOfDesign: String? = null,
    @SerializedName("formatted_price")
    @Expose
    var formattedPrice: String? = null

)