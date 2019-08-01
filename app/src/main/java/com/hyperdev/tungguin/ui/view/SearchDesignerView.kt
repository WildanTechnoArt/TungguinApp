package com.hyperdev.tungguin.ui.view

import com.hyperdev.tungguin.model.detailorder.DetailOrderData

class SearchDesignerView {

    interface View {
        fun showDetailOrder(data: DetailOrderData?)
        fun displayProgress()
        fun hideProgress()
        fun handleError(e: Throwable)
    }

    interface Presenter {
        fun getDetailOrder(token: String, id: String)
        fun onDestroy()
    }
}