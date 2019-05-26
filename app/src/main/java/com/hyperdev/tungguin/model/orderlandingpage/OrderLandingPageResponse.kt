package com.hyperdev.tungguin.model.orderlandingpage

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.hyperdev.tungguin.network.ErrorResponse

data class OrderLandingPageResponse (

    @SerializedName("meta")
    @Expose
    var meta: ErrorResponse? = null,
    @SerializedName("data")
    @Expose
    var data: OrderData? = null

)