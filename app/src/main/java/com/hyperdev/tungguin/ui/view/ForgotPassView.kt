package com.hyperdev.tungguin.ui.view

import android.content.Context

class ForgotPassView {

    interface View {
        fun showProgressBar()
        fun hideProgressBar()
        fun onSuccess()
        fun handleError(e: Throwable)
    }

    interface Presenter {
        fun forgotPassword(email: String, context: Context)
        fun onDestroy()
    }
}