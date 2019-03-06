package com.hyperdev.tungguin.repository

import com.hyperdev.tungguin.model.profile.ProfileResponse
import io.reactivex.Flowable

interface ProfileRepository {
    fun getProfile(token: String) : Flowable<ProfileResponse>
}