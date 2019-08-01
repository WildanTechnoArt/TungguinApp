package com.hyperdev.tungguin.presenter

import com.hyperdev.tungguin.model.detailorder.DetailOrderResponse
import com.hyperdev.tungguin.model.profile.ProfileResponse
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.ui.view.DetailOrderView
import com.hyperdev.tungguin.utils.SchedulerProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subscribers.ResourceSubscriber

class DetailOrderPresenter(
    private val view: DetailOrderView.View,
    private val baseApiService: BaseApiService,
    private val scheduler: SchedulerProvider
) : DetailOrderView.Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun getUserProfile(token: String) {

        compositeDisposable.add(
            baseApiService.getProfile(token, "application/json")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(scheduler.io())
                .subscribeWith(object : ResourceSubscriber<ProfileResponse>() {
                    override fun onComplete() {
                        view.hideProgress()
                        view.onSuccess()
                    }

                    override fun onNext(t: ProfileResponse) {
                        try {
                            t.data?.let { view.displayProfile(it) }
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }
                    }

                    override fun onError(e: Throwable) {
                        view.handleError(e)
                    }
                })
        )
    }

    override fun getDetailOrder(token: String, id: String) {
        view.displayProgress()

        compositeDisposable.add(
            baseApiService.getOrderDetail(token, "application/json", id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(scheduler.io())
                .subscribeWith(object : ResourceSubscriber<DetailOrderResponse>() {
                    override fun onComplete() {}

                    override fun onNext(t: DetailOrderResponse) {
                        try {
                            view.showDetailOrder(t.data)
                            t.data?.items?.let { view.showDesignItem(it) }
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