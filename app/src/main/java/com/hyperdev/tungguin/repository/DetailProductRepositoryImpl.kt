package com.hyperdev.tungguin.repository

import com.hyperdev.tungguin.model.detailproduct.DetailProductResponse
import com.hyperdev.tungguin.network.BaseApiService
import io.reactivex.Flowable

class DetailProductRepositoryImpl (private val baseApiService: BaseApiService): DetailProductRepository{
    override fun getDetailProduct(token: String, accept: String, hashed_id: String): Flowable<DetailProductResponse> = baseApiService.getDetailProduct(token, accept, hashed_id)
}