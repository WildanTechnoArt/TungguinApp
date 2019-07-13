package com.hyperdev.tungguin.ui.view

import com.hyperdev.tungguin.model.profile.DataUser
import com.hyperdev.tungguin.model.transaction.DataTopUp

class TopUpView {

    interface View {
        fun displayAmount(profileItem: DataUser)
        fun transactionData(data: DataTopUp)
        fun showProgressBar()
        fun hideProgressBar()
        fun onSuccess()
        fun noInternetConnection(message: String)
    }

    interface Presenter {
        fun getUserAmount(token: String)
        fun topUpMoney(token: String, accept: String, amount: String)
        fun onDestroy()
    }
}