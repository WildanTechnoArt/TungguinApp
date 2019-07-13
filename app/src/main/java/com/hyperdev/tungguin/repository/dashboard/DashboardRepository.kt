package com.hyperdev.tungguin.repository.dashboard

import com.hyperdev.tungguin.model.dashboard.*
import com.hyperdev.tungguin.model.profile.ProfileResponse
import io.reactivex.Flowable
import io.reactivex.Observable

interface DashboardRepository {

    // Digunakan untuk mendapatkan beberapa data user seperti nama dan jumlan uang pada wallet
    fun postTokenFcm(token: String, accept: String, tokenFcm: String?): Observable<FCMResponse>

    // Method untuk mengirim token notifikasi dari Firebase
    fun getProfile(token: String, accept: String): Flowable<ProfileResponse>

    // Method untuk menampilkan gambar slider pada dashboard
    fun getImageSlider(token: String): Flowable<DashboardResponse>

    // Method untuk menampilkan pengumuman dari admin
    fun announcementDesigner(token: String): Flowable<AnnouncementResponse>

}