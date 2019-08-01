package com.hyperdev.tungguin.ui.view

import com.hyperdev.tungguin.model.transaction.ListTransaction
import com.hyperdev.tungguin.model.transaction.TransactionHistory

class HistoryTransactionView {

    interface View {
        fun showTransactionHistory(dataTransaction: List<ListTransaction>)
        fun showTransaction(transaction: TransactionHistory)
        fun displayProgress()
        fun onSuccess()
        fun hideProgress()
        fun handleError(e: Throwable)
    }

    interface Presenter {
        fun getTransactionHistory(token: String, page: Int)
        fun onDestroy()
    }
}