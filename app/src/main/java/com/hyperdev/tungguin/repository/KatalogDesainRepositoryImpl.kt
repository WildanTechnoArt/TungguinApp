package com.hyperdev.tungguin.repository

import com.hyperdev.tungguin.model.katalogdesain.KatalogDesainResponse
import com.hyperdev.tungguin.network.BaseApiService
import io.reactivex.Flowable

class KatalogDesainRepositoryImpl (private val baseApiService: BaseApiService): KatalogDesainRepository{
    override fun getKatalogDesain(token: String, accept: String): Flowable<KatalogDesainResponse> = baseApiService.getKatalogDesain(token, accept)
}