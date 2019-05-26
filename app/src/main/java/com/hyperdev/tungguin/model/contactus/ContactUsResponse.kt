package com.hyperdev.tungguin.model.contactus

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ContactUsResponse (
    @SerializedName("meta")
    @Expose
    var meta: MetaContactUs? = null
)