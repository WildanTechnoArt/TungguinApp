package com.hyperdev.tungguin.repository

import com.hyperdev.tungguin.model.detailproduct.DetailProductResponse
import io.reactivex.Flowable

interface DetailProductRepository {
    fun getDetailProduct(token: String, accept: String, hashed_id: String) : Flowable<DetailProductResponse>
}