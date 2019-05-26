package com.hyperdev.tungguin.model.checkkupon

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DataKupon (

    @SerializedName("is_available")
    @Expose
    var fisAvailable: Boolean? = null,

    @SerializedName("content")
    @Expose
    var content: String? = null

)