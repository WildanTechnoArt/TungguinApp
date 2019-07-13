package com.hyperdev.tungguin.repository.authentication

import com.hyperdev.tungguin.model.authentication.LoginResponse
import com.hyperdev.tungguin.model.authentication.CityResponse
import com.hyperdev.tungguin.model.authentication.ProvinceResponse
import com.hyperdev.tungguin.model.authentication.RegisterResponse
import com.hyperdev.tungguin.network.BaseApiService
import io.reactivex.Flowable
import io.reactivex.Observable

class AuthRepositoryImp(private val baseApiService: BaseApiService) : AuthRepository {

    // Method untuk melakukan permintaan masuk/login
    override fun loginRequest(accept: String, email: String, password: String): Observable<LoginResponse> =
        baseApiService.loginRequest(accept, email, password)

    // Method untuk mendaftarkan user baru
    override fun registerRequest(accept: String, register: HashMap<String, String>): Observable<RegisterResponse> =
        baseApiService.registerRequest(accept, register)

    // Method untuk menampilkan semua nama provinsi di Indonesia
    override fun getProvince(): Flowable<ProvinceResponse> = baseApiService.getAllProvince()

    // Method untuk menampilkan semua nama kota di Indonesia
    override fun getCity(id: String): Flowable<CityResponse> = baseApiService.getAllCity(id)

}