package com.hyperdev.tungguin.model.register

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.hyperdev.tungguin.model.Data
import com.hyperdev.tungguin.network.ErrorResponse

data class RegisterResponse (

    @SerializedName("data")
    @Expose
    var getData: Data? = null,

    @SerializedName("meta")
    @Expose
    var getMeta: ErrorResponse? = null
)
