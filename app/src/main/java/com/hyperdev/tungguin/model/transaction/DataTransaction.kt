package com.hyperdev.tungguin.model.transaction

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DataTransaction (
    @SerializedName("balance")
    @Expose
    var balance: String? = null,
    @SerializedName("transaction_history")
    @Expose
    var transactionHistory: TransactionHistory? = null
)