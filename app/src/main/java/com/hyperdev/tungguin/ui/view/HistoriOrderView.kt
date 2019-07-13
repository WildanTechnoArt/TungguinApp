package com.hyperdev.tungguin.ui.view

import com.hyperdev.tungguin.model.historiorder.DataOrder
import com.hyperdev.tungguin.model.historiorder.OrderItem

class HistoriOrderView {

    interface View {
        fun showOrderHistory(data: List<OrderItem>)
        fun showOrderData(order: DataOrder)
        fun displayProgress()
        fun onSuccess()
        fun hideProgress()
    }

    interface Presenter {
        fun getOrderHistory(token: String, page: Int)
        fun onDestroy()
    }
}