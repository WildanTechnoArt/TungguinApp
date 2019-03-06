package com.hyperdev.tungguin.view

import android.content.Context
import com.hyperdev.tungguin.model.topuphistori.ListTopUp

class HistoriTopUpView {

    interface View{
        fun showTopUpHistory(data: List<ListTopUp>)
        fun displayProgress()
        fun hideProgress()
    }

    interface Presenter{
        fun getTopUpHistory(context: Context, token: String, page: Int)
        fun onDestroy()
    }
}