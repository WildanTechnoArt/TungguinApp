package com.hyperdev.tungguin.repository

import com.hyperdev.tungguin.model.register.CityResponse
import com.hyperdev.tungguin.model.register.ProvinceResponse
import com.hyperdev.tungguin.network.BaseApiService
import io.reactivex.Flowable

class RegisterRepositoryImpl (private val baseApiService: BaseApiService): RegisterRepository {
    override fun getProvinceAll(): Flowable<ProvinceResponse> = baseApiService.getAllProvince()
    override fun getCityAll(id: String): Flowable<CityResponse> = baseApiService.getAllCity(id)
}