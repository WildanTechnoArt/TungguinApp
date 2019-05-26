package com.hyperdev.tungguin.view

import com.hyperdev.tungguin.model.detailorder.OrderDetailItem

class ReviewOrderView {

    interface View{
        fun showDetailOrder(data: OrderDetailItem?)
        fun displayProgress()
        fun hideProgress()
        fun onSuccess()
    }

    interface Presenter{
        fun getDetailOrder(token: String, id: String)
        fun onDestroy()
    }
}