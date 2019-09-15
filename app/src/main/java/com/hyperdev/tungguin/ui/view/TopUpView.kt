package com.hyperdev.tungguin.ui.view

import com.hyperdev.tungguin.model.profile.DataUser
import com.hyperdev.tungguin.model.topup.DataTopUp
import com.hyperdev.tungguin.model.topup.TopUpItemData

class TopUpView {

    interface View {
        fun displayAmount(profileItem: DataUser)
        fun transactionData(data: DataTopUp)
        fun showTopupAmount(amount: List<TopUpItemData>)
        fun showProgressBar()
        fun hideProgressBar()
        fun onSuccess()
        fun handleError(e: Throwable)
    }

    interface Presenter {
        fun getUserAmount(token: String)
        fun topUpMoney(token: String, accept: String, amount: String)
        fun topUpAmount(token: String, accept: String)
        fun onDestroy()
    }
}