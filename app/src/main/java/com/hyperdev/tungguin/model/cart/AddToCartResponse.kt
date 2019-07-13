package com.hyperdev.tungguin.model.cart

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.hyperdev.tungguin.network.ErrorResponse

class AddToCartResponse{

    @SerializedName("meta")
    @Expose
    var meta: ErrorResponse? = null
    @SerializedName("data")
    @Expose
    var data: AddCartData? = null

}