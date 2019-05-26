package com.hyperdev.tungguin.model.katalogdesain

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.hyperdev.tungguin.network.ErrorResponse

data class KatalogDesainResponse (

    @SerializedName("meta")
    @Expose
    var meta: ErrorResponse? = null,
    @SerializedName("data")
    @Expose
    var data: List<KatalogItem>? = null

)