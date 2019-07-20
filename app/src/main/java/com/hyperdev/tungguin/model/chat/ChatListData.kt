package com.hyperdev.tungguin.model.chat

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ChatListData(

    @SerializedName("current_page")
    @Expose
    var current_page: Int? = null,

    @SerializedName("data")
    @Expose
    var chatListItem: ArrayList<ChatListItem>? = null,

    @SerializedName("first_page_url")
    @Expose
    var first_page_url: String? = null,

    @SerializedName("from")
    @Expose
    var from: Int? = null,

    @SerializedName("last_page")
    @Expose
    var last_page: Int? = null,

    @SerializedName("last_page_url")
    @Expose
    var last_page_url: String? = null,

    @SerializedName("next_page_url")
    @Expose
    var next_page_url: String? = null,

    @SerializedName("path")
    @Expose
    var path: String? = null,

    @SerializedName("prev_page_url")
    @Expose
    var prev_page_url: String? = null,

    @SerializedName("to")
    @Expose
    var to: Int? = null,

    @SerializedName("total")
    @Expose
    var total: Int? = null

)