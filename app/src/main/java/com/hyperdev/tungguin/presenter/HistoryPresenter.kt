package com.hyperdev.tungguin.presenter

import com.hyperdev.tungguin.model.transaction.TransactionResponse
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.ui.view.BalanceView
import com.hyperdev.tungguin.utils.SchedulerProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subscribers.ResourceSubscriber

class HistoryPresenter(
    private val view: BalanceView.View,
    private val baseApiService: BaseApiService,
    private val scheduler: SchedulerProvider
) : BalanceView.Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun getUserBalance(token: String) {
        view.showProgressBar()

        compositeDisposable.add(
            baseApiService.getTransactionBalance(token, "application/json")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(scheduler.io())
                .subscribeWith(object : ResourceSubscriber<TransactionResponse>() {
                    override fun onComplete() {
                        view.onSuccess()
                    }

                    override fun onNext(t: TransactionResponse) {
                        try {
                            t.data?.let { view.showTransaction(it) }
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }
                    }

                    override fun onError(e: Throwable) {
                        view.hideProgressBar()
                        view.handleError(e)
                    }
                })
        )
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
    }
}