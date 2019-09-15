package com.hyperdev.tungguin.model.topup

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.hyperdev.tungguin.network.ErrorResponse

data class TopUpResponse (
    @SerializedName("meta")
    @Expose
    var getMeta: ErrorResponse? = null,

    @SerializedName("data")
    @Expose
    var getData: DataTopUp? = null
)