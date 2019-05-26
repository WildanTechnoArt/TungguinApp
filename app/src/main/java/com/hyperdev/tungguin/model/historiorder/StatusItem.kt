package com.hyperdev.tungguin.model.historiorder

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class StatusItem (

    @SerializedName("status")
    @Expose
    var status: String? = null,

    @SerializedName("label")
    @Expose
    var label: String? = null
)