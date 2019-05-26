package com.hyperdev.tungguin.repository

import com.hyperdev.tungguin.model.chat.ChatHistoriResponse
import com.hyperdev.tungguin.model.detailorder.DetailOrderResponse
import io.reactivex.Flowable

interface ChatHistoriRepository {
    fun getOrderDetail(token: String, accept: String, hashed_id: String) : Flowable<DetailOrderResponse>
    fun getChatHistori(token: String, accept: String, hashed_id: String, page: Int) : Flowable<ChatHistoriResponse>
}