package com.hyperdev.tungguin.model.topup

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DataTopUp (
    @SerializedName("midtrans_token")
    @Expose
    val getMitransToken: String? = null,

    @SerializedName("formatted_id")
    @Expose
    val getFormattedId: String? = null,

    @SerializedName("formatted_amount")
    @Expose
    val getFormattedAmount: String? = null,

    @SerializedName("formatted_date")
    @Expose
    val getFormattedDate: String? = null,

    @SerializedName("formatted_status")
    @Expose
    val getFormattedStatus: String? = null,

    @SerializedName("formatted_expire_at")
    @Expose
    val getExpireAt: String? = null
)