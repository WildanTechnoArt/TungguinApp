package com.hyperdev.tungguin.ui.view

import com.hyperdev.tungguin.model.transaction.DataTransaction

class BalanceView {

    interface View {
        fun showTransaction(transactionItem: DataTransaction)
        fun showProgressBar()
        fun hideProgressBar()
        fun onSuccess()
    }

    interface Presenter {
        fun getUserBalance(token: String)
        fun onDestroy()
    }
}