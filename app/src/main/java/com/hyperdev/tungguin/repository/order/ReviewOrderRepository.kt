package com.hyperdev.tungguin.repository.order

import com.hyperdev.tungguin.model.detailorder.DetailOrderResponse
import io.reactivex.Flowable
import io.reactivex.Observable

interface ReviewOrderRepository {

    // Digunakan untuk menmpilkan Detail Order pada halaman Testimoni
    fun getOrderDetail(token: String, accept: String, hashed_id: String): Flowable<DetailOrderResponse>

    // Metdod untuk mengirim testomoni pada Designer yang bersangkutan
    fun sendTestimoni(
        token: String, accept: String,
        hashed_id: String, star: String,
        designer_testi: String, app_testi: String,
        tip: String
    ): Observable<DetailOrderResponse>

}