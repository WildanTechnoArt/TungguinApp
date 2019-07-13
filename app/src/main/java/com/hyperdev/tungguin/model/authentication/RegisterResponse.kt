package com.hyperdev.tungguin.model.authentication

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.hyperdev.tungguin.model.dashboard.DashboardData
import com.hyperdev.tungguin.network.ErrorResponse

data class RegisterResponse(

    @SerializedName("data")
    @Expose
    var getData: DashboardData? = null,

    @SerializedName("meta")
    @Expose
    var getMeta: ErrorResponse? = null
)
