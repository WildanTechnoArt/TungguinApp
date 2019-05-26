package com.hyperdev.tungguin.model.topuphistori

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.hyperdev.tungguin.network.ErrorResponse

data class HistoriTopUpResponse (
    @SerializedName("meta")
    @Expose
    private var meta: ErrorResponse? = null,
    @SerializedName("data")
    @Expose var data: TopUpData? = null
)