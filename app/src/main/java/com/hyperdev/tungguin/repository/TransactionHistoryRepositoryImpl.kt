package com.hyperdev.tungguin.repository

import com.hyperdev.tungguin.model.transactionhistory.TransactionResponse
import com.hyperdev.tungguin.network.BaseApiService
import io.reactivex.Flowable

class TransactionHistoryRepositoryImpl (private val baseApiService: BaseApiService): TransactionHIstoryRepository {

    override fun getTransactionHistory(authHeader: String, page: Int): Flowable<TransactionResponse> =
        baseApiService.getTransactionHistory(authHeader, page)

    override fun getTransactionBalance(authHeader: String): Flowable<TransactionResponse> =
        baseApiService.getTransactionBalance(authHeader)
}