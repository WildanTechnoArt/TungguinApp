package com.hyperdev.tungguin.repository

import com.hyperdev.tungguin.model.detailorder.DetailOrderResponse
import io.reactivex.Flowable

interface ReviewOrderRepository {
    fun getOrderDetail(token: String, accept: String,hashed_id: String) : Flowable<DetailOrderResponse>
}