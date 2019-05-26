package com.hyperdev.tungguin.model.checkout

import com.google.gson.annotations.SerializedName
import com.hyperdev.tungguin.network.ErrorResponse

data class CheckoutResponse (

    @SerializedName("meta")
    var meta: ErrorResponse? = null,

    @SerializedName("data")
    var checkoutItem: CheckoutData? = null
)