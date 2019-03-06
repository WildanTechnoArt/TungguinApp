package com.hyperdev.tungguin.repository

import com.hyperdev.tungguin.model.profile.ProfileResponse
import com.hyperdev.tungguin.model.topuphistori.HistoriTopUpResponse
import io.reactivex.Flowable

interface TopUpHIstoryRepository {
    fun getTopUpHistory(authHeader: String, page: Int) : Flowable<HistoriTopUpResponse>
    fun getProfile(token: String) : Flowable<ProfileResponse>
}