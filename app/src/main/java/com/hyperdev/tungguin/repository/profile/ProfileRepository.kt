package com.hyperdev.tungguin.repository.profile

import com.hyperdev.tungguin.model.profile.ProfileResponse
import io.reactivex.Flowable
import io.reactivex.Observable

interface ProfileRepository {

    // Method untuk menampilkan dara profil user
    fun getProfile(token: String, accept: String): Flowable<ProfileResponse>

    // Method untuk update atau edit profil user
    fun updateProfile(
        token: String, accept: String, name: String, email: String,
        phone: String, province: String, city: String
    ): Observable<ProfileResponse>

    // Method untuk mengubah password user
    fun updatePassword(
        token: String, accept: String, name: String, email: String,
        phone: String, province: String, city: String, password: String,
        c_password: String
    ): Observable<ProfileResponse>

}