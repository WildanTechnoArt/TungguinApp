package com.hyperdev.tungguin.presenter

import com.hyperdev.tungguin.model.detailorder.DetailOrderResponse
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.ui.view.ReviewOrderView
import com.hyperdev.tungguin.utils.SchedulerProvider
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subscribers.ResourceSubscriber

class ReviewOrderPresenter(
    private val view: ReviewOrderView.View,
    private val baseApiService: BaseApiService,
    private val scheduler: SchedulerProvider
) : ReviewOrderView.Presenter {

    override fun sendTestimoni(
        token: String,
        accept: String,
        hashed_id: String,
        star: String,
        designer_testi: String,
        app_testi: String,
        tip: String
    ) {

        view.displayProgress()

        baseApiService.sendReview(token, accept, hashed_id, star, designer_testi, app_testi, tip)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(scheduler.io())
            .unsubscribeOn(scheduler.io())
            .subscribe(object : Observer<DetailOrderResponse> {
                override fun onComplete() {
                    view.onSuccessSend()
                }

                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onNext(t: DetailOrderResponse) {}

                override fun onError(e: Throwable) {
                    view.hideProgress()
                    view.handleError(e)
                }

            })
    }

    private val compositeDisposable = CompositeDisposable()

    override fun getDetailOrder(token: String, id: String) {
        view.displayProgress()

        compositeDisposable.add(
            baseApiService.getOrderDetail(token, "application/json", id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(scheduler.io())
                .subscribeWith(object : ResourceSubscriber<DetailOrderResponse>() {
                    override fun onComplete() {
                        view.onSuccess()
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