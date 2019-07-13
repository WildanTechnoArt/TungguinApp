package com.hyperdev.tungguin.repository.chat

import com.hyperdev.tungguin.model.chat.*
import com.hyperdev.tungguin.model.detailorder.DetailOrderResponse
import io.reactivex.Flowable
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface ChatRepository {

    // Digunakan untuk menampilkan Nama dan Foto Designer (diambil dari detail order) pada halaman chat
    fun getOrderDetail(token: String, accept: String, hashed_id: String): Flowable<DetailOrderResponse>

    // Method ini digunakna untuk menampilkan chat atau histori chat antara customer dan designer
    fun getChatHistori(token: String, accept: String, hashed_id: String, page: Int): Flowable<ChatHistoriResponse>

    // Method untuk mengirim pesan dari customer ke designer
    fun chatRequest(
        token: String, accept: String, hashed_id: String,
        text: RequestBody, file: MultipartBody.Part?
    ): Observable<MessageModel>

}