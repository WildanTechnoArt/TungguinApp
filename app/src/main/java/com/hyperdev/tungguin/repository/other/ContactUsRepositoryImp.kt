package com.hyperdev.tungguin.repository.other

import com.hyperdev.tungguin.model.contact.ContactUsResponse
import com.hyperdev.tungguin.network.BaseApiService
import io.reactivex.Observable

class ContactUsRepositoryImp(private val baseApiService: BaseApiService) : ContactUsRepository {

    override fun contactUs(
        token: String, accept: String,
        title: String, content: String
    ): Observable<ContactUsResponse> = baseApiService.contactUs(token, accept, title, content)

}