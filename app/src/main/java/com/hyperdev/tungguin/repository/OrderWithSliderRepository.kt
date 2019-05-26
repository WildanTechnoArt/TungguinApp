package com.hyperdev.tungguin.repository

import com.hyperdev.tungguin.model.orderlandingpage.OrderLandingPageResponse
import io.reactivex.Flowable

interface OrderWithSliderRepository {
    fun getOrderWithSlider(token: String) : Flowable<OrderLandingPageResponse>
}