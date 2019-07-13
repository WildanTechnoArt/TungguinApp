package com.hyperdev.tungguin.model.dashboard

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DashboardData (
    @SerializedName("token")
    @Expose
    var token: String? = null
)