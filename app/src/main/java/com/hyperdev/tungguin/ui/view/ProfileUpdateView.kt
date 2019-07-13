package com.hyperdev.tungguin.ui.view

class ProfileUpdateView {

    interface View {
        fun showProgressBar()
        fun hideProgressBar()
        fun onSuccess()
        fun noInternetConnection(message: String)
    }

    interface Presenter {
        fun updateProfile(
            token: String, accept: String,
            name: String, email: String, phone: String,
            province: String, city: String
        )

        fun updatePassword(
            token: String, accept: String,
            name: String, email: String, phone: String,
            province: String, city: String, password: String,
            c_password: String
        )

        fun onDestroy()
    }
}