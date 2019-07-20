package com.hyperdev.tungguin.repository.chat

import com.hyperdev.tungguin.model.chat.*
import com.hyperdev.tungguin.model.detailorder.DetailOrderResponse
import com.hyperdev.tungguin.network.BaseApiService
import io.reactivex.Flowable
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ChatRepositoryImp(private val baseApiService: BaseApiService) : ChatRepository {

    // Digunakan untuk menampilkan Nama dan Foto Designer (diambil dari detail order) pada halaman chat
    override fun getOrderDetail(token: String, accept: String, hashed_id: String): Flowable<DetailOrderResponse> =
        baseApiService.getOrderDetail(token, accept, hashed_id)

    // Method ini digunakna untuk menampilkan chat atau histori chat antara customer dan designer
    override fun getChatHistori(
        token: String,
        accept: String,
        hashed_id: String,
        page: Int?
    ): Flowable<ChatHistoryResponse> = baseApiService.getChatHistori(token, accept, hashed_id, page)

    // Digunakan untuk menampilkan daftar Chat Designer yang sedang aktif
    override fun getChatList(token: String, accept: String, page: Int?): Flowable<ChatListResponse> =
        baseApiService.getChat(token, accept, page)

    // Method untuk mengirim pesan dari customer ke designer
    override fun chatRequest(
        token: String,
        accept: String,
        hashed_id: String,
        text: RequestBody,
        file: MultipartBody.Part?
    ): Observable<MessageModel> = baseApiService.chatRequest(token, accept, hashed_id, text, file)

}