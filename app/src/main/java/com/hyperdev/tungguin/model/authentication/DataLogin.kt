package com.hyperdev.tungguin.model.authentication

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DataLogin(
    @SerializedName("token")
    @Expose
    var token: String? = null
)