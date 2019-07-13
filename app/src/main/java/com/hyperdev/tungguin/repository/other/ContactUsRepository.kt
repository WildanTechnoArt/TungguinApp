package com.hyperdev.tungguin.repository.other

import com.hyperdev.tungguin.model.contact.ContactUsResponse
import io.reactivex.Observable

interface ContactUsRepository {
    fun contactUs(token: String, accept: String, title: String, content: String): Observable<ContactUsResponse>
}