package com.hyperdev.tungguin.model.detailorder

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.hyperdev.tungguin.model.historiorder.StatusItem

data class OrderDetailItem (

    @SerializedName("payment_type")
    @Expose
    var paymentType: String? = null,

    @SerializedName("midtrans_token")
    @Expose
    var midtransToken: String? = null,

    @SerializedName("pdf_url")
    @Expose
    var pdfUrl: String? = null,

    @SerializedName("formatted_id")
    @Expose
    var formattedId: String? = null,

    @SerializedName("hashed_id")
    @Expose
    var hashedId: String? = null,

    @SerializedName("real_total_formatted")
    @Expose
    var realTotalFormatted: String? = null,

    @SerializedName("formatted_date")
    @Expose
    var formattedDate: String? = null,

    @SerializedName("expire_date_formatted")
    @Expose
    var expireDateFormatted: String? = null,

    @SerializedName("status_formatted")
    @Expose
    var statusFormatted: StatusItem? = null,

    @SerializedName("designer")
    @Expose
    var designer: DesainerProfile? = null,

    @SerializedName("items")
    @Expose
    var items: List<ItemDesign>? = null,

    @SerializedName("result")
    @Expose
    var result: List<ResultItem>? = null,

    @SerializedName("testimonial")
    @Expose
    var testimonial: TestimoniItem? = null

)