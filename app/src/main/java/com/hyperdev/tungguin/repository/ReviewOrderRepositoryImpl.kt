package com.hyperdev.tungguin.repository

import com.hyperdev.tungguin.model.detailorder.DetailOrderResponse
import com.hyperdev.tungguin.network.BaseApiService
import io.reactivex.Flowable

class ReviewOrderRepositoryImpl (private val baseApiService: BaseApiService): ReviewOrderRepository{
    override fun getOrderDetail(token: String, accept: String, hashed_id: String): Flowable<DetailOrderResponse> = baseApiService.getOrderDetail(token, accept, hashed_id)
}