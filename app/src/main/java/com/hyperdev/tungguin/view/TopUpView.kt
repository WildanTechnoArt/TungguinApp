package com.hyperdev.tungguin.view

import android.content.Context
import com.hyperdev.tungguin.model.profile.DataUser

class TopUpView {

    interface View{
        fun displayAmount(profileItem: DataUser)
        fun displayProgress()
        fun hideProgress()
    }

    interface Presenter{
        fun getUserAmount(context: Context, token: String)
        fun onDestroy()
    }
}