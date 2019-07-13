package com.hyperdev.tungguin.ui.view

class ForgotPassView {

    interface View {
        fun showProgressBar()
        fun hideProgressBar()
        fun onSuccess()
        fun noInternetConnection(message: String)
    }

    interface Presenter {
        fun forgotPassword(email: String)
        fun onDestroy()
    }
}