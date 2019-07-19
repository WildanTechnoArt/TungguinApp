package com.hyperdev.tungguin.repository.order

import com.hyperdev.tungguin.model.detailorder.DetailOrderResponse
import com.hyperdev.tungguin.network.BaseApiService
import io.reactivex.Flowable
import io.reactivex.Observable

class ReviewOrderRepositoryImpl(private val baseApiService: BaseApiService) : ReviewOrderRepository {

    // Digunakan untuk menmpilkan Detail Order pada halaman Testimoni
    override fun sendTestimoni(
        token: String,
        accept: String,
        hashed_id: String,
        star: String,
        designer_testi: String,
        app_testi: String,
        tip: String
    ): Observable<DetailOrderResponse> =
        baseApiService.sendReview(token, accept, hashed_id, star, designer_testi, app_testi, tip)

    // Metdod untuk mengirim testomoni pada Designer yang bersangkutan
    override fun getOrderDetail(token: String, accept: String, hashed_id: String): Flowable<DetailOrderResponse> =
        baseApiService.getOrderDetail(token, accept, hashed_id)
}