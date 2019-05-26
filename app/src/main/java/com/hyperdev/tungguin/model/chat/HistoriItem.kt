package com.hyperdev.tungguin.model.chat

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class HistoriItem (

    @SerializedName("data")
    @Expose
    var dataChat: List<ChatData>? = null,

    @SerializedName("next_page_url")
    @Expose
    var nextPageUrl: String? = null
)