package com.hyperdev.tungguin.model.detailorder

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class TestimoniItem (

    @SerializedName("order_id")
    @Expose
    var orderId: Int? = null,

    @SerializedName("star_rating")
    @Expose
    var starRating: Int? = null,

    @SerializedName("designer_testimonial")
    @Expose
    var designerTestimonial: String? = null,

    @SerializedName("app_testimonial")
    @Expose
    var appTestimonial: String? = null,

    @SerializedName("designer_tip_formatted")
    @Expose
    var designerTipFormatted: String? = null

)