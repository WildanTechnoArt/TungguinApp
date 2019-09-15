package com.hyperdev.tungguin.model.topup

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class TopUpItemData (
    @SerializedName("amount")
    @Expose var amount: Long? = null,

    @SerializedName("label")
    @Expose var label: String? = null
)