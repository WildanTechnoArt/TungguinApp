package com.hyperdev.tungguin.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Data (
    @SerializedName("token")
    @Expose
    var token: String? = null
)