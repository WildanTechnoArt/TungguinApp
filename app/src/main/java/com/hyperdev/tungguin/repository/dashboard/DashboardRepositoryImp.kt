package com.hyperdev.tungguin.repository.dashboard

import com.hyperdev.tungguin.model.dashboard.*
import com.hyperdev.tungguin.model.profile.ProfileResponse
import com.hyperdev.tungguin.network.BaseApiService
import io.reactivex.Flowable
import io.reactivex.Observable

class DashboardRepositoryImp(private val baseApiService: BaseApiService) : DashboardRepository {

    // Digunakan untuk mendapatkan beberapa data user seperti nama dan jumlan uang pada wallet
    override fun getProfile(token: String, accept: String): Flowable<ProfileResponse> =
        baseApiService.getProfile(token, accept)

    // Method untuk mengirim token notifikasi dari Firebase
    override fun postTokenFcm(token: String, accept: String, tokenFcm: String?): Observable<FCMResponse> =
        baseApiService.fcmRequest(token, accept, tokenFcm)

    // Method untuk menampilkan gambar slider pada dashboard
    override fun getImageSlider(token: String): Flowable<DashboardResponse> = baseApiService.getDashboardSlider(token)

    // Method untuk menampilkan pengumuman dari admin
    override fun announcementDesigner(token: String): Flowable<AnnouncementResponse> =
        baseApiService.announcementDesigner(token)
}