package com.hyperdev.tungguin.ui.view

import com.hyperdev.tungguin.model.detailorder.ItemDesign
import com.hyperdev.tungguin.model.detailorder.OrderDetailItem
import com.hyperdev.tungguin.model.profile.DataUser

class DetailOrderView {

    interface View {
        fun showDetailOrder(data: OrderDetailItem?)
        fun displayProfile(profileItem: DataUser)
        fun showDesignItem(orderList: List<ItemDesign>)
        fun displayProgress()
        fun hideProgress()
        fun onSuccess()
    }

    interface Presenter {
        fun getDetailOrder(token: String, id: String)
        fun getUserProfile(token: String)
        fun onDestroy()
    }
}