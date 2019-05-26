package com.hyperdev.tungguin.view

import com.hyperdev.tungguin.model.orderlandingpage.OrderData
import com.hyperdev.tungguin.model.profile.DataUser

class OrderWithSliderView {

    interface View{
        fun displayOrderWithSlider(orderItem: List<String>)
        fun displayOnlineDesigner(designerItem: OrderData)
        fun displayProfile(profileItem: DataUser)
        fun displayProgress()
        fun hideProgress()
    }

    interface Presenter{
        fun getOrderWithSlider(token: String)
        fun getUserProfile(token: String)
        fun onDestroy()
    }
}