package com.hyperdev.tungguin.repository

import com.hyperdev.tungguin.model.cart.CartResponse
import com.hyperdev.tungguin.network.BaseApiService
import io.reactivex.Flowable

class CartRepositoryImpl (private val baseApiService: BaseApiService): CartRepository{
    override fun getCartData(token: String, accept: String): Flowable<CartResponse> = baseApiService.getCartData(token, accept)
}