package com.hyperdev.tungguin.repository

import com.hyperdev.tungguin.model.searchproduct.SearchByNameResponse
import io.reactivex.Flowable

interface SearchProductRepository {
    fun searchProuctByName(authHeader: String, accept: String, name: String) : Flowable<SearchByNameResponse>
    fun searchProuctByPage(authHeader: String, accept: String, page: Int) : Flowable<SearchByNameResponse>
}