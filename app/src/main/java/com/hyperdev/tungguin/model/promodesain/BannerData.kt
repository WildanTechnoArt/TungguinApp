package com.hyperdev.tungguin.model.promodesain

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BannerData (

    @SerializedName("title")
    @Expose
    var title: String? = null,

    @SerializedName("url")
    @Expose
    var url: String? = null,

    @SerializedName("banner_url")
    @Expose
    var bannerUrl: String? = null

)