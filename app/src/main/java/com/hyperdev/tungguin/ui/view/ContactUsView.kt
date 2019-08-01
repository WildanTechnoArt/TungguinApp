package com.hyperdev.tungguin.ui.view

import com.hyperdev.tungguin.model.contact.MetaContactUs

class ContactUsView {

    interface View {
        fun contactUsMessage(message: MetaContactUs)
        fun showProgressBar()
        fun hideProgressBar()
        fun onSuccess()
        fun handleError(e: Throwable)
    }

    interface Presenter {
        fun contactUs(token: String, accept: String, title: String, content: String)
        fun onDestroy()
    }
}