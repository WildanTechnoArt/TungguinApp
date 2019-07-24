package com.hyperdev.tungguin.model.profile

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DataUser (

    @SerializedName("name")
    @Expose
    var name: String? = null,

    @SerializedName("email")
    @Expose
    var email: String? = null,

    @SerializedName("password")
    @Expose
    var password: String? = null,

    @SerializedName("phone_number")
    @Expose
    var phoneNumber: String? = null,

    @SerializedName("cart_count")
    @Expose
    var cartCount: Int? = null,

    @SerializedName("active_order_count")
    @Expose
    var activeOrderCount: Int? = null,

    @SerializedName("formatted_balance")
    @Expose
    var formattedBalance: String? = null,

    @SerializedName("province")
    @Expose
    var province: Province? = null,

    @SerializedName("city")
    @Expose
    var city: City? = null
)