package com.hyperdev.tungguin.view

import com.hyperdev.tungguin.model.transactionhistory.DataTransaction

class BalanceView {

    interface View{
        fun displayTransaction(transactionItem: DataTransaction)
        fun displayProgress()
        fun hideProgress()
    }

    interface Presenter{
        fun getUserBalance(token: String)
        fun onDestroy()
    }
}