package com.hyperdev.tungguin.view

import android.content.Context
import com.hyperdev.tungguin.model.profile.DataUser

class DashboardView {

    interface View{
        fun displayProfile(profileItem: DataUser)
        fun displayProgress()
        fun hideProgress()
    }

    interface Presenter{
        fun getUserProfile(context: Context, token: String)
        fun onDestroy()
    }
}