package com.hyperdev.tungguin.model.transactionhistory

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class MetaTransaction (
    @SerializedName("topup_id")
    @Expose
    var topupId: Int? = null,
    @SerializedName("description")
    @Expose
    var description: String? = null
)