package com.hyperdev.tungguin.model.cart

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DeleteCartData(

    @SerializedName("items")
    @Expose
    var items: List<DeleteCartItem>? = null,
    @SerializedName("sub_total")
    @Expose
    var subTotal: String? = null,
    @SerializedName("service_fee")
    @Expose
    var serviceFee: String? = null,
    @SerializedName("total")
    @Expose
    var total: String? = null,
    @SerializedName("disable_add_to_cart")
    @Expose
    var disableAddToCart: Boolean? = null

)