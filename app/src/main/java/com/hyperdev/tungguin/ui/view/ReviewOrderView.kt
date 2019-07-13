package com.hyperdev.tungguin.ui.view

import com.hyperdev.tungguin.model.detailorder.OrderDetailItem

class ReviewOrderView {

    interface View {
        fun showDetailOrder(data: OrderDetailItem?)
        fun displayProgress()
        fun hideProgress()
        fun onSuccess()
        fun onSuccessSend()
        fun noInternetConnection(message: String)
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