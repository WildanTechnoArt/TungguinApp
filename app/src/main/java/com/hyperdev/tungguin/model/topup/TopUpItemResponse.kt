package com.hyperdev.tungguin.model.topup

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.hyperdev.tungguin.network.ErrorResponse

data class TopUpItemResponse (
    @SerializedName("meta")
    @Expose
    private var meta: ErrorResponse? = null,

    @SerializedName("data")
    @Expose var data: List<TopUpItemData>? = null
)