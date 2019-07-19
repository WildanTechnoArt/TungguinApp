package com.hyperdev.tungguin.model.authentication

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ProvinceItem(

    @SerializedName("id")
    @Expose
    var id: Int? = null,

    @SerializedName("name")
    @Expose
    var name: String? = null

)