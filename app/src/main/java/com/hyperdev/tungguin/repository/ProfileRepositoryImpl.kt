package com.hyperdev.tungguin.repository

import com.hyperdev.tungguin.model.profile.ProfileResponse
import com.hyperdev.tungguin.network.BaseApiService
import io.reactivex.Flowable

class ProfileRepositoryImpl (private val baseApiService: BaseApiService): ProfileRepository {
    override fun getProfile(token: String): Flowable<ProfileResponse> = baseApiService.getProfile(token)
}