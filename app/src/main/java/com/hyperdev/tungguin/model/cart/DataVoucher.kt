package com.hyperdev.tungguin.model.cart

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DataVoucher(

    @SerializedName("is_available")
    @Expose
    var isAvailable: Boolean? = null,

    @SerializedName("message")
    @Expose
    var message: String? = null

)