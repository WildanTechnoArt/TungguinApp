package com.hyperdev.tungguin.ui.view

import com.hyperdev.tungguin.model.detailorder.DetailOrderData

class ReviewOrderView {

    interface View {
        fun showDetailOrder(data: DetailOrderData?)
        fun displayProgress()
        fun hideProgress()
        fun onSuccess()
        fun onSuccessSend()
        fun handleError(e: Throwable)
    }

    interface Presenter {
        fun getDetailOrder(token: String, id: String)
        fun sendTestimoni(
            token: String, accept: String,
            hashed_id: String, star: String,
            designer_testi: String, app_testi: String,
            tip: String
        )

        fun onDestroy()
    }
}