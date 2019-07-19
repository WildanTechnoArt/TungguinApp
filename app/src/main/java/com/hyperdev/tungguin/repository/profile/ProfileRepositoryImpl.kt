package com.hyperdev.tungguin.repository.profile

import com.hyperdev.tungguin.model.profile.ProfileResponse
import com.hyperdev.tungguin.network.BaseApiService
import io.reactivex.Flowable
import io.reactivex.Observable

class ProfileRepositoryImpl(private val baseApiService: BaseApiService) : ProfileRepository {

    // Method untuk menampilkan dara profil user
    override fun getProfile(token: String, accept: String): Flowable<ProfileResponse> =
        baseApiService.getProfile(token, accept)

    // Method untuk update atau edit profil user
    override fun updateProfile(
        token: String,
        accept: String,
        name: String,
        email: String,
        phone: String,
        province: String,
        city: String
    ): Observable<ProfileResponse> =
        baseApiService.updateProfile(token, accept, name, email, phone, province, city)

    // Method untuk mengubah password user
    override fun updatePassword(
        token: String,
        accept: String,
        name: String,
        email: String,
        phone: String,
        province: String,
        city: String,
        password: String,
        c_password: String
    ): Observable<ProfileResponse> =
        baseApiService.updatePassword(token, accept, name, email, phone, province, city, password, c_password)

}