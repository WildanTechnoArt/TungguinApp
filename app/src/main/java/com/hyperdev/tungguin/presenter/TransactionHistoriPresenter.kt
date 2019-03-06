package com.hyperdev.tungguin.presenter

import android.content.Context
import com.hyperdev.tungguin.model.transactionhistory.TransactionResponse
import com.hyperdev.tungguin.repository.TransactionHistoryRepositoryImpl
import com.hyperdev.tungguin.utils.SchedulerProvider
import com.hyperdev.tungguin.view.HistoriTransactionView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subscribers.ResourceSubscriber

//Digunakan untuk menjembatani Model dengan View pada Fragment
class TransactionHistoriPresenter(private val view: HistoriTransactionView.View,
                                  private val history: TransactionHistoryRepositoryImpl,
                                  private val scheduler: SchedulerProvider) : HistoriTransactionView.Presenter{

    private val compositeDisposable = CompositeDisposable()

    override fun getTransactionHistory(context: Context, token: String, page: Int) {
        view.displayProgress()

        compositeDisposable.add(history.getTransactionHistory(token, page)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(scheduler.io())
            .subscribeWith(object : ResourceSubscriber<TransactionResponse>(){
                override fun onComplete() {
                    view.hideProgress()
                }

                override fun onNext(t: TransactionResponse) {
                    try{
                        t.data?.let { it.transactionHistory?.dataTransaction
                            ?.let { it1 -> view.showTransactionHistory(it1) } }
                    }catch(ex: Exception){
                        ex.printStackTrace()
                    }
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    view.hideProgress()
                }
            })
        )
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
    }
}