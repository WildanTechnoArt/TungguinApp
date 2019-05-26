package com.hyperdev.tungguin.repository

import com.hyperdev.tungguin.model.searchproduct.SearchByNameResponse
import com.hyperdev.tungguin.network.BaseApiService
import io.reactivex.Flowable

class SearchProductRepositoryImpl (private val baseApiService: BaseApiService): SearchProductRepository {
    override fun searchProuctByName(authHeader: String, accept: String, name: String): Flowable<SearchByNameResponse>
            = baseApiService.searchProuctByName(authHeader, accept, name)
    override fun searchProuctByPage(authHeader: String, accept: String, page: Int): Flowable<SearchByNameResponse>
            = baseApiService.searchProuctByPage(authHeader, accept, page)
}