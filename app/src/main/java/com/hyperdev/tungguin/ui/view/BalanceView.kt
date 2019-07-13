package com.hyperdev.tungguin.ui.view

import com.hyperdev.tungguin.model.transaction.DataTransaction

class BalanceView {

    interface View {
        fun displayTransaction(transactionItem: DataTransaction)
        fun displayProgress()
        fun hideProgress()
    }

    interface Presenter {
        fun getUserBalance(token: String)
        fun onDestroy()
    }
}