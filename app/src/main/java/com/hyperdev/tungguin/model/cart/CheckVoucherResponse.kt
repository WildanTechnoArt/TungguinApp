package com.hyperdev.tungguin.model.cart

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.hyperdev.tungguin.network.ErrorResponse

data class CheckVoucherResponse(

    @SerializedName("meta")
    @Expose
    var meta: ErrorResponse? = null,

    @SerializedName("data")
    @Expose
    var kuponData: DataVoucher? = null

)