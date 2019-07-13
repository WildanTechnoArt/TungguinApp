package com.hyperdev.tungguin.model.contact

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ContactUsResponse (
    @SerializedName("meta")
    @Expose
    var meta: MetaContactUs? = null
)