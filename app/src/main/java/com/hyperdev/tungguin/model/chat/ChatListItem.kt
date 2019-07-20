package com.hyperdev.tungguin.model.chat

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.hyperdev.tungguin.model.detailorder.DesignerData

data class ChatListItem (

    @SerializedName("hashed_id")
    @Expose
    var hashedId: String? = null,

    @SerializedName("formatted_id")
    @Expose
    var formattedId: String? = null,

    @SerializedName("designer")
    @Expose
    var designer: DesignerData? = null

)