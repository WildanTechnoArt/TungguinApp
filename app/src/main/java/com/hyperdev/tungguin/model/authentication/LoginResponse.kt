package com.hyperdev.tungguin.model.authentication

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.hyperdev.tungguin.network.ErrorResponse

data class LoginResponse(
    @SerializedName("data")
    @Expose
    var getData: DataLogin? = null,

    @SerializedName("meta")
    @Expose
    var getMeta: ErrorResponse? = null
)