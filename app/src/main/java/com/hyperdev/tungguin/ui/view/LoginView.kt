package com.hyperdev.tungguin.ui.view

import android.content.Context

class LoginView {

    interface View {
        fun showProgressBar()
        fun hideProgressBar()
        fun onSuccess()
        fun handleError(e: Throwable)
    }

    interface Presenter {
        fun loginUser(email: String, password: String, context: Context)
        fun onDestroy()
    }
}