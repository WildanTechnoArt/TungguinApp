package com.hyperdev.tungguin.ui.view

import com.hyperdev.tungguin.model.topup.ListTopUp
import com.hyperdev.tungguin.model.topup.TopUpData

class HistoryTopUpView {

    interface View {
        fun showTopUpHistory(data: List<ListTopUp>)
        fun showTopUp(topup: TopUpData)
        fun displayProgress()
        fun onSuccess()
        fun hideProgress()
        fun handleError(e: Throwable)
    }

    interface Presenter {
        fun getTopUpHistory(token: String, page: Int)
        fun onDestroy()
    }
}