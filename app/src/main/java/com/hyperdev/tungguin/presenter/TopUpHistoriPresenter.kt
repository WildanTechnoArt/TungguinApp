package com.hyperdev.tungguin.presenter

import com.hyperdev.tungguin.model.transaction.HistoriTopUpResponse
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.ui.view.HistoryTopUpView
import com.hyperdev.tungguin.utils.SchedulerProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subscribers.ResourceSubscriber

class TopUpHistoriPresenter(
    private val view: HistoryTopUpView.View,
    private val baseApiService: BaseApiService,
    private val scheduler: SchedulerProvider
) : HistoryTopUpView.Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun getTopUpHistory(token: String, page: Int) {
        view.displayProgress()

        compositeDisposable.add(
            baseApiService.getTopUpHistory(token, "application/json", page)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(scheduler.io())
                .subscribeWith(object : ResourceSubscriber<HistoriTopUpResponse>() {
                    override fun onComplete() {
                        view.onSuccess()
                    }

                    override fun onNext(t: HistoriTopUpResponse) {
                        try {
                            t.data?.let { it.listTopUp?.let { it1 -> view.showTopUpHistory(it1) } }
                            t.data?.let { view.showTopUp(it) }
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