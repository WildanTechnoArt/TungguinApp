package com.hyperdev.tungguin.repository

import com.hyperdev.tungguin.model.transactionhistory.TransactionResponse
import io.reactivex.Flowable

interface TransactionHIstoryRepository {
    fun getTransactionHistory(authHeader: String, accept: String, page: Int) : Flowable<TransactionResponse>
    fun getTransactionBalance(authHeader: String, accept: String) : Flowable<TransactionResponse>
}