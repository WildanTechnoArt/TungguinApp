package com.hyperdev.tungguin.repository

import com.hyperdev.tungguin.model.historiorder.HistoriOrderResponse
import io.reactivex.Flowable

interface OrderHIstoryRepository {
    fun getOrderHistory(authHeader: String, accept: String, page: Int) : Flowable<HistoriOrderResponse>
}