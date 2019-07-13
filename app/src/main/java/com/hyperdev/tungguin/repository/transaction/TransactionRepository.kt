package com.hyperdev.tungguin.repository.transaction

import com.hyperdev.tungguin.model.transaction.*
import io.reactivex.Flowable
import io.reactivex.Observable

// Repository untuk transaksi order atau top up
interface TransactionRepository {

    // Method untuk melihat histori top up
    fun getTopUpHistory(token: String, accept: String, page: Int): Flowable<HistoriTopUpResponse>

    // Method untuk top up atau menyimpan uang ke wallet
    fun topUpMoney(token: String, accept: String, amount: String): Observable<TopUpResponse>

    // Method untuk melihat transaksi order
    fun getTransactionHistory(token: String, accept: String, page: Int) : Flowable<TransactionResponse>

    // Method untuk melihat jumlah uang yang ada di wallet
    fun getTransactionBalance(token: String, accept: String) : Flowable<TransactionResponse>

}