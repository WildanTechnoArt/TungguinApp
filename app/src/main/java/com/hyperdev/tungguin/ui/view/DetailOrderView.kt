package com.hyperdev.tungguin.ui.view

import com.hyperdev.tungguin.model.detailorder.ItemDesign
import com.hyperdev.tungguin.model.detailorder.DetailOrderData
import com.hyperdev.tungguin.model.profile.DataUser

class DetailOrderView {

    interface View {
        fun showDetailOrder(data: DetailOrderData?)
        fun displayProfile(profileItem: DataUser)
        fun showDesignItem(orderList: List<ItemDesign>)
        fun displayProgress()
        fun hideProgress()
        fun onSuccess()
        fun handleError(e: Throwable)
    }

    interface Presenter {
        fun getDetailOrder(token: String, id: String)
        fun getUserProfile(token: String)
        fun onDestroy()
    }
}