package com.hyperdev.tungguin.model.detailorder

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ResultItem (

    @SerializedName("path_url")
    @Expose
    var pathUrl: String? = null

)