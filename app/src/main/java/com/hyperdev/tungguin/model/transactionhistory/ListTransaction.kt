package com.hyperdev.tungguin.model.transactionhistory

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ListTransaction (
    @SerializedName("type")
    @Expose
    var type: String? = null,
    @SerializedName("meta")
    @Expose
    var meta: MetaTransaction? = null,
    @SerializedName("formatted_amount")
    @Expose
    var formattedAmount: String? = null,
    @SerializedName("formatted_date")
    @Expose
    var formattedDate: String? = null
)