package com.hyperdev.tungguin.model.detailorder

import com.google.gson.JsonObject
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ItemDesign (

    @SerializedName("design_concept")
    @Expose
    var designConcept : String? = null,

    @SerializedName("product_name")
    @Expose
    var productName : String? = null,

    @SerializedName("formatted_number_of_design")
    @Expose
    var formattedNumber : String? = null,

    @SerializedName("formatted_price")
    @Expose
    var formattedPrice : String? = null,

    @SerializedName("design_document_url")
    @Expose
    var designDocumentUrl : String? = null,

    @SerializedName("design_preference_array")
    @Expose
    var preferenceArray : List<String>? = null,

    @SerializedName("design_detail_object")
    @Expose
    var designDetailObject : JsonObject? = null
)