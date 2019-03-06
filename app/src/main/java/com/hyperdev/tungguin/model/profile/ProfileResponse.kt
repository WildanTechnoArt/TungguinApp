package com.hyperdev.tungguin.model.profile

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.hyperdev.tungguin.network.ErrorResponse

data class ProfileResponse (
    @SerializedName("data")
    @Expose
    var data: DataUser? = null,

    @SerializedName("meta")
    @Expose
    var getMeta: ErrorResponse? = null
)