package com.hyperdev.tungguin.repository.transaction

import com.hyperdev.tungguin.model.transaction.*
import com.hyperdev.tungguin.network.BaseApiService
import io.reactivex.Flowable
import io.reactivex.Observable

class TransactionRepositoryImp(private val baseApiService: BaseApiService) : TransactionRepository {

    override fun getTopUpHistory(token: String, accept: String, page: Int): Flowable<HistoriTopUpResponse> =
        baseApiService.getTopUpHistory(token, accept, page)

    override fun topUpMoney(token: String, accept: String, amount: String): Observable<TopUpResponse> =
        baseApiService.topUpMoney(token, accept, amount)

    override fun getTransactionHistory(token: String, accept: String, page: Int): Flowable<TransactionResponse> =
        baseApiService.getTransactionHistory(token, accept, page)

    override fun getTransactionBalance(token: String, accept: String): Flowable<TransactionResponse> =
        baseApiService.getTransactionBalance(token, accept)

}