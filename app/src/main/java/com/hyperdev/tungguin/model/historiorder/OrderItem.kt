package com.hyperdev.tungguin.model.historiorder

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class OrderItem (

    @SerializedName("is_paid")
    @Expose
    var isPaid: Boolean? = null,

    @SerializedName("formatted_id")
    @Expose
    var formattedId: String? = null,

    @SerializedName("real_total_formatted")
    @Expose
    var realTotalFormatted: String? = null,

    @SerializedName("status_formatted")
    @Expose
    var statusFormatted: StatusItem? = null,

    @SerializedName("hashed_id")
    @Expose
    var hashedId: String? = null

)