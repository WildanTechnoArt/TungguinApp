package com.hyperdev.tungguin.presenter

import com.hyperdev.tungguin.model.detailorder.DetailOrderResponse
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.ui.view.SearchDesignerView
import com.hyperdev.tungguin.utils.SchedulerProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subscribers.ResourceSubscriber

class SearchDesignPresenter(
    private val view: SearchDesignerView.View,
    private val baseApiService: BaseApiService,
    private val scheduler: SchedulerProvider
) : SearchDesignerView.Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun getDetailOrder(token: String, id: String) {
        view.displayProgress()

        compositeDisposable.add(
            baseApiService.getOrderDetail(token, "application/json", id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(scheduler.io())
                .subscribeWith(object : ResourceSubscriber<DetailOrderResponse>() {
                    override fun onComplete() {
                        view.hideProgress()
                    }

                    override fun onNext(t: DetailOrderResponse) {
                        try {
                            view.showDetailOrder(t.data)
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