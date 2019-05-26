package com.hyperdev.tungguin.repository

import com.hyperdev.tungguin.model.orderlandingpage.OrderLandingPageResponse
import com.hyperdev.tungguin.network.BaseApiService
import io.reactivex.Flowable

class OrderWithSliderRepositoryImpl (private val baseApiService: BaseApiService): OrderWithSliderRepository{
    override fun getOrderWithSlider(token: String): Flowable<OrderLandingPageResponse> = baseApiService.getOrderWithSlider(token)
}