package com.hyperdev.tungguin.presenter

import com.hyperdev.tungguin.model.transaction.TransactionResponse
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.ui.view.HistoryTransactionView
import com.hyperdev.tungguin.utils.SchedulerProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subscribers.ResourceSubscriber

class TransactionHistoriPresenter(
    private val view: HistoryTransactionView.View,
    private val baseApiService: BaseApiService,
    private val scheduler: SchedulerProvider
) : HistoryTransactionView.Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun getTransactionHistory(token: String, page: Int) {
        view.displayProgress()

        compositeDisposable.add(
            baseApiService.getTransactionHistory(token, "application/json", page)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(scheduler.io())
                .subscribeWith(object : ResourceSubscriber<TransactionResponse>() {
                    override fun onComplete() {
                        view.onSuccess()
                    }

                    override fun onNext(t: TransactionResponse) {
                        try {
                            t.data?.let {
                                it.transactionHistory?.dataTransaction
                                    ?.let { it1 -> view.showTransactionHistory(it1) }
                            }
                            t.data?.transactionHistory?.let { view.showTransaction(it) }
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }
                    }

                    override fun onError(e: Throwable) {
                        view.hideProgress()
                        view.handleError(e)
                    }
                })
        )
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
    }
}