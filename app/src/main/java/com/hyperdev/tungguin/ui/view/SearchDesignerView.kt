package com.hyperdev.tungguin.ui.view

import com.hyperdev.tungguin.model.detailorder.OrderDetailItem

class SearchDesignerView {

    interface View {
        fun showDetailOrder(data: OrderDetailItem?)
        fun displayProgress()
        fun hideProgress()
    }

    interface Presenter {
        fun getDetailOrder(token: String, id: String)
        fun onDestroy()
    }
}