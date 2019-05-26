package com.hyperdev.tungguin.model.announcement

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AnnouncementData (

    @SerializedName("announcement")
    @Expose
    var announcement: String? = null
)