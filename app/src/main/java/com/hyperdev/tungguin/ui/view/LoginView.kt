package com.hyperdev.tungguin.ui.view

class LoginView {

    interface View {
        fun showProgressBar()
        fun hideProgressBar()
        fun onSuccess()
        fun noInternetConnection(message: String)
    }

    interface Presenter {
        fun loginUser(email: String, password: String)
        fun onDestroy()
    }
}