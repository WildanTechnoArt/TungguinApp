package com.hyperdev.tungguin.model.transaction

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class TransactionResponse (
    @SerializedName("data")
    @Expose var data: DataTransaction? = null
)