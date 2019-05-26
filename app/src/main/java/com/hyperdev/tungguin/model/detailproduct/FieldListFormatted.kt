package com.hyperdev.tungguin.model.detailproduct

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class FieldListFormatted (

    @SerializedName("name")
    @Expose
    var name: String? = null,
    @SerializedName("type")
    @Expose
    var type: String? = null,
    @SerializedName("placeholder")
    @Expose
    var placeholder: String? = null,
    @SerializedName("required")
    @Expose
    var required: Boolean? = null,
    @SerializedName("options")
    @Expose
    var options: List<Any>? = null,
    @SerializedName("key")
    @Expose
    var key: String? = null
)