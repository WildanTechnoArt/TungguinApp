package com.hyperdev.tungguin.view

import android.content.Context
import com.hyperdev.tungguin.model.transactionhistory.DataTransaction

class BalanceView {

    interface View{
        fun displayTransaction(transactionItem: DataTransaction)
        fun displayProgress()
        fun hideProgress()
    }

    interface Presenter{
        fun getUserBalance(context: Context, token: String)
        fun onDestroy()
    }
}