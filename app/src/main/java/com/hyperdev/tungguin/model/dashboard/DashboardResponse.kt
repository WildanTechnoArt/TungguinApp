package com.hyperdev.tungguin.model.dashboard

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.hyperdev.tungguin.network.ErrorResponse

data class DashboardResponse (

    @SerializedName("meta")
    @Expose
    var meta: ErrorResponse? = null,

    @SerializedName("data")
    @Expose
    var data: ArrayList<String>? = null

)