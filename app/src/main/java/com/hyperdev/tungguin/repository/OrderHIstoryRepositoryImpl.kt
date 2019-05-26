package com.hyperdev.tungguin.repository

import com.hyperdev.tungguin.model.historiorder.HistoriOrderResponse
import com.hyperdev.tungguin.network.BaseApiService
import io.reactivex.Flowable

class OrderHIstoryRepositoryImpl (private val baseApiService: BaseApiService): OrderHIstoryRepository {
    override fun getOrderHistory(authHeader: String, accept: String, page: Int): Flowable<HistoriOrderResponse> = baseApiService.getOrderHistori(authHeader, accept, page)
}