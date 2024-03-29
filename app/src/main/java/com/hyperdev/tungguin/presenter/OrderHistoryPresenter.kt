package com.hyperdev.tungguin.presenter

import com.hyperdev.tungguin.model.historiorder.HistoriOrderResponse
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.ui.view.HistoryOrderView
import com.hyperdev.tungguin.utils.SchedulerProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subscribers.ResourceSubscriber

class OrderHistoryPresenter(
    private val view: HistoryOrderView.View,
    private val baseApiService: BaseApiService,
    private val scheduler: SchedulerProvider
) : HistoryOrderView.Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun getOrderHistory(token: String, page: Int) {
        view.displayProgress()

        compositeDisposable.add(
            baseApiService.getOrderHistori(token, "application/json", page)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(scheduler.io())
                .subscribeWith(object : ResourceSubscriber<HistoriOrderResponse>() {
                    override fun onComplete() {
                        view.onSuccess()
                    }

                    override fun onNext(t: HistoriOrderResponse) {
                        try {
                            t.data.let { it.orderItem?.let { it1 -> view.showOrderHistory(it1) } }
                            view.showOrderData(t.data)
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