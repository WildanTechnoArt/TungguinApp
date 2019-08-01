package com.hyperdev.tungguin.ui.view

import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddToCartView {

    interface View {
        fun showProgressBar()
        fun hideProgressBar()
        fun onSuccess()
        fun handleError(e: Throwable)
    }

    interface Presenter {
        fun addToCart(
            token: String, accept: String, cartMap: HashMap<String, RequestBody>,
            file: MutableList<MultipartBody.Part>, fileDoc: MultipartBody.Part?,
            price: RequestBody?
        )

        fun onDestroy()
    }
}