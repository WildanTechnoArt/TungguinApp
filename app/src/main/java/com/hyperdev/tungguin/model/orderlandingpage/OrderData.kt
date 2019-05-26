package com.hyperdev.tungguin.model.orderlandingpage

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class OrderData (

    @SerializedName("available_designer")
    @Expose
    var availableDesigner: String? = null,
    @SerializedName("slider_image")
    @Expose
    var sliderImage: ArrayList<String>? = null

)