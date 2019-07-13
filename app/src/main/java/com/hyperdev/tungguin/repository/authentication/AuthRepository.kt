package com.hyperdev.tungguin.repository.authentication

import com.hyperdev.tungguin.model.authentication.*
import io.reactivex.Flowable
import io.reactivex.Observable

interface AuthRepository {

    // Method untuk melakukan permintaan masuk/login
    fun loginRequest(accept: String, email: String, password: String): Observable<LoginResponse>

    // Method untuk mendaftarkan user baru
    fun registerRequest(accept: String, register: HashMap<String, String>): Observable<RegisterResponse>

    // Method untuk menampilkan semua nama provinsi di Indonesia
    fun getProvince(): Flowable<ProvinceResponse>

    // Method untuk menampilkan semua nama kota di Indonesia
    fun getCity(id: String): Flowable<CityResponse>

}