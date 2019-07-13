package com.hyperdev.tungguin.model.contact

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class MetaContactUs (
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