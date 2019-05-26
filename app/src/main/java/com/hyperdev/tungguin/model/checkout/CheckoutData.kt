package com.hyperdev.tungguin.model.checkout

import com.google.gson.annotations.SerializedName

data class CheckoutData (

    @SerializedName("midtrans_token")
    var midtransToken: String? = null,

    @SerializedName("formatted_id")
    var formattedId: String? = null,

    @SerializedName("hashed_id")
    var hashedId: String? = null,

    @SerializedName("real_total_formatted")
    var totalFormatted: String? = null

)