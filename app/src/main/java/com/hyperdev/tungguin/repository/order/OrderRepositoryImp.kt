package com.hyperdev.tungguin.repository.order

import com.hyperdev.tungguin.model.detailorder.DetailOrderResponse
import com.hyperdev.tungguin.model.historiorder.HistoriOrderResponse
import com.hyperdev.tungguin.model.orderlandingpage.OrderLandingPageResponse
import com.hyperdev.tungguin.model.profile.ProfileResponse
import com.hyperdev.tungguin.network.BaseApiService
import io.reactivex.Flowable

class OrderRepositoryImp(private val baseApiService: BaseApiService) : OrderRepository {

    // Method untuk menampilkan profil user pada detail order
    override fun getProfile(token: String, accept: String): Flowable<ProfileResponse> =
        baseApiService.getProfile(token, accept)

    // Method untuk menampilkan detail order dari product (desain) yang dipesan
    override fun getOrderDetail(token: String, accept: String, hashed_id: String): Flowable<DetailOrderResponse> =
        baseApiService.getOrderDetail(token, accept, hashed_id)

    // Method untuk menampilkan histori order
    override fun getOrderHistory(token: String, accept: String, page: Int): Flowable<HistoriOrderResponse> =
        baseApiService.getOrderHistori(token, accept, page)

    // Method untuk menampilkan gambar slider pada halaman order
    override fun getOrderWithSlider(token: String): Flowable<OrderLandingPageResponse> =
        baseApiService.getOrderWithSlider(token)

}