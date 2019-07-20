package com.hyperdev.tungguin.model.detailorder

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DesignerData (

    @SerializedName("name")
    @Expose
    var name: String? = null,

    @SerializedName("formatted_id")
    @Expose
    var formattedId: String? = null,

    @SerializedName("is_internal")
    @Expose
    var isInternal: Boolean? = null,

    @SerializedName("phone_number")
    @Expose
    var phoneNumber: String? = null,

    @SerializedName("photo_url")
    @Expose
    var photoUrl: String? = null,

    @SerializedName("hashed_id")
    @Expose
    var hashedId: String? = null

)