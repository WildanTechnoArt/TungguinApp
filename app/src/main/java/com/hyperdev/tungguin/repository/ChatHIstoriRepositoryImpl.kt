package com.hyperdev.tungguin.repository

import com.hyperdev.tungguin.model.chat.ChatHistoriResponse
import com.hyperdev.tungguin.model.detailorder.DetailOrderResponse
import com.hyperdev.tungguin.network.BaseApiService
import io.reactivex.Flowable

class ChatHIstoriRepositoryImpl (private val baseApiService: BaseApiService): ChatHistoriRepository{
    override fun getOrderDetail(token: String, accept: String, hashed_id: String): Flowable<DetailOrderResponse> = baseApiService.getOrderDetail(token, accept, hashed_id)
    override fun getChatHistori(token: String, accept: String, hashed_id: String, page: Int): Flowable<ChatHistoriResponse> = baseApiService.getChatHistori(token, accept, hashed_id, page)
}