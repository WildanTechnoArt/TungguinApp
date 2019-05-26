package com.hyperdev.tungguin.repository

import com.hyperdev.tungguin.model.detailorder.DetailOrderResponse
import com.hyperdev.tungguin.model.profile.ProfileResponse
import com.hyperdev.tungguin.network.BaseApiService
import io.reactivex.Flowable

class DetailOrderRepositoryImpl (private val baseApiService: BaseApiService): DetailOrderRepository{
    override fun getProfile(token: String, accept: String): Flowable<ProfileResponse> = baseApiService.getProfile(token, accept)
    override fun getOrderDetail(token: String, accept: String, hashed_id: String): Flowable<DetailOrderResponse> = baseApiService.getOrderDetail(token, accept, hashed_id)
}