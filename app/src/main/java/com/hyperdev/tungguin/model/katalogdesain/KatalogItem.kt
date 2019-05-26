package com.hyperdev.tungguin.model.katalogdesain

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class KatalogItem (

    @SerializedName("label")
    @Expose
    var label: String? = null,
    @SerializedName("items")
    @Expose
    var items: List<Item>? = null

)