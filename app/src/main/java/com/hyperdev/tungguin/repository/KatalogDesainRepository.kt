package com.hyperdev.tungguin.repository

import com.hyperdev.tungguin.model.katalogdesain.KatalogDesainResponse
import io.reactivex.Flowable

interface KatalogDesainRepository {
    fun getKatalogDesain(token: String, accept: String) : Flowable<KatalogDesainResponse>
}