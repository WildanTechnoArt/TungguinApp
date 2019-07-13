package com.hyperdev.tungguin.model.authentication

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CityResponse(

    @SerializedName("data")
    @Expose
    var cityList: List<CityItem>? = null
)