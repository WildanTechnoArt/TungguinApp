package com.hyperdev.tungguin.repository.other

import com.hyperdev.tungguin.model.authentication.LoginResponse
import com.hyperdev.tungguin.network.BaseApiService
import io.reactivex.Observable

class ForgotPassRepositoryImpl(private val baseApiService: BaseApiService) : ForgorPassRepository {

    override fun forgotPassword(accept: String, email: String): Observable<LoginResponse> =
        baseApiService.forgotPassword(accept, email)

}