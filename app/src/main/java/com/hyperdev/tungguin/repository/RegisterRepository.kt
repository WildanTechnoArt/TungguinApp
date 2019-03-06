package com.hyperdev.tungguin.repository

import com.hyperdev.tungguin.model.register.CityResponse
import com.hyperdev.tungguin.model.register.ProvinceResponse
import io.reactivex.Flowable

interface RegisterRepository {
    fun getProvinceAll() : Flowable<ProvinceResponse>
    fun getCityAll(id: String) : Flowable<CityResponse>
}