package com.hyperdev.tungguin.repository

import com.hyperdev.tungguin.model.deletecart.DeleteCartResponse
import io.reactivex.Flowable

interface DeleteCartRepository {
    fun getCartDelete(token: String, accept: String, hashed_id: String) : Flowable<DeleteCartResponse>
}