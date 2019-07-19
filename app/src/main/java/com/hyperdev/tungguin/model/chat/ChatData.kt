package com.hyperdev.tungguin.model.chat

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ChatData (

    @SerializedName("send_from_designer")
    @Expose
    var type: Int? = null,

    @SerializedName("text")
    @Expose
    var text: String? = null,

    @SerializedName("file")
    @Expose
    var file: String? = null,

    @SerializedName("formatted_date")
    @Expose
    var formattedDate: String? = null,

    @SerializedName("sender")
    @Expose
    var sender: String? = null,

    @SerializedName("file_type")
    @Expose
    var fileType: String? = null
)