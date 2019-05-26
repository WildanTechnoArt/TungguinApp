package com.hyperdev.tungguin.repository

import com.hyperdev.tungguin.model.cart.CartResponse
import io.reactivex.Flowable

interface CartRepository {
    fun getCartData(token: String, accept: String) : Flowable<CartResponse>
}