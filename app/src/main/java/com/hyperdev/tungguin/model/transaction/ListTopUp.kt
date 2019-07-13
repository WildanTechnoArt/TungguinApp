package com.hyperdev.tungguin.model.transaction

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ListTopUp (
    @SerializedName("midtrans_token")
    @Expose
    var midtransToken: String? = null,
    @SerializedName("formatted_id")
    @Expose
    var formattedId: String? = null,
    @SerializedName("formatted_amount")
    @Expose
    var formattedAmount: String? = null,
    @SerializedName("formatted_date")
    @Expose
    var formattedDate: String? = null,
    @SerializedName("formatted_status")
    @Expose
    var formattedStatus: String? = null,
    @SerializedName("status_color_hex")
    @Expose
    var statusColorHex: String? = null,
    @SerializedName("formatted_expire_at")
    @Expose
    var formattedExpireAt: String? = null
)