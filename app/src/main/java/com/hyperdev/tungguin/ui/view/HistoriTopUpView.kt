package com.hyperdev.tungguin.ui.view

import com.hyperdev.tungguin.model.transaction.ListTopUp
import com.hyperdev.tungguin.model.transaction.TopUpData

class HistoriTopUpView {

    interface View {
        fun showTopUpHistory(data: List<ListTopUp>)
        fun showTopUp(topup: TopUpData)
        fun displayProgress()
        fun onSuccess()
        fun hideProgress()
    }

    interface Presenter {
        fun getTopUpHistory(token: String, page: Int)
        fun onDestroy()
    }
}