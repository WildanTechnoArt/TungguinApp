package com.hyperdev.tungguin.repository.other

import com.hyperdev.tungguin.model.authentication.LoginResponse
import io.reactivex.Observable

interface ForgorPassRepository {
    fun forgotPassword(accept: String, email: String): Observable<LoginResponse>
}