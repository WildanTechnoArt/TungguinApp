package com.hyperdev.tungguin.repository.order

import com.hyperdev.tungguin.model.detailorder.DetailOrderResponse
import com.hyperdev.tungguin.model.historiorder.HistoriOrderResponse
import com.hyperdev.tungguin.model.orderlandingpage.OrderLandingPageResponse
import com.hyperdev.tungguin.model.profile.ProfileResponse
import io.reactivex.Flowable

interface OrderRepository {

    // Method untuk menampilkan profil user pada detail order
    fun getProfile(token: String, accept: String): Flowable<ProfileResponse>

    // Method untuk menampilkan detail order dari produk (desain) yang dipesan
    fun getOrderDetail(token: String, accept: String, hashed_id: String): Flowable<DetailOrderResponse>

    // Method untuk menampilkan histori order
    fun getOrderHistory(token: String, accept: String, page: Int): Flowable<HistoriOrderResponse>

    // Method untuk mengorder desain dengan menampilkan Image Slider
    fun getOrderWithSlider(token: String): Flowable<OrderLandingPageResponse>

}