package com.hyperdev.tungguin.model.register

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ProvinceResponse (

    @SerializedName("data")
    @Expose
    var provinceList: List<ProvinceItem>? = null
)