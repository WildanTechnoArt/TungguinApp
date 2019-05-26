package com.hyperdev.tungguin.repository

import com.hyperdev.tungguin.model.detailorder.DetailOrderResponse
import com.hyperdev.tungguin.model.profile.ProfileResponse
import io.reactivex.Flowable

interface DetailOrderRepository {
    fun getProfile(token: String, accept: String) : Flowable<ProfileResponse>
    fun getOrderDetail(token: String, accept: String, hashed_id: String) : Flowable<DetailOrderResponse>
}