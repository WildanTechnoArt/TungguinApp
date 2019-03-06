package com.hyperdev.tungguin.repository

import com.hyperdev.tungguin.model.profile.ProfileResponse
import com.hyperdev.tungguin.model.topuphistori.HistoriTopUpResponse
import com.hyperdev.tungguin.network.BaseApiService
import io.reactivex.Flowable

class TopUpHIstoryRepositoryImpl (private val baseApiService: BaseApiService): TopUpHIstoryRepository {
    override fun getTopUpHistory(authHeader: String, page: Int): Flowable<HistoriTopUpResponse> = baseApiService.getTopUpHistory(authHeader, page)
    override fun getProfile(token: String): Flowable<ProfileResponse> = baseApiService.getProfile(token)
}