package com.hyperdev.tungguin.repository

import com.hyperdev.tungguin.model.deletecart.DeleteCartResponse
import com.hyperdev.tungguin.network.BaseApiService
import io.reactivex.Flowable

class DeleteCartRepositoryImpl (private val baseApiService: BaseApiService): DeleteCartRepository{
    override fun getCartDelete(token: String, accept: String, hashed_id: String): Flowable<DeleteCartResponse> = baseApiService.getCartDalete(token, accept, hashed_id)
}