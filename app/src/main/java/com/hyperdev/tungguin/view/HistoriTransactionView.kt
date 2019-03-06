package com.hyperdev.tungguin.view

import android.content.Context
import com.hyperdev.tungguin.model.transactionhistory.ListTransaction

class HistoriTransactionView {

    interface View{
        fun showTransactionHistory(dataTransaction: List<ListTransaction>)
        fun displayProgress()
        fun hideProgress()
    }

    interface Presenter{
        fun getTransactionHistory(context: Context, token: String, page: Int)
        fun onDestroy()
    }
}