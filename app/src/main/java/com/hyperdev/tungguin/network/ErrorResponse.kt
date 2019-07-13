package com.hyperdev.tungguin.network

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @SerializedName("code")
    @Expose
    var code: Int? = null,
    @SerializedName("status")
    @Expose
    var status: String? = null,
    @SerializedName("message")
    @Expose
    var message: String? = null
)