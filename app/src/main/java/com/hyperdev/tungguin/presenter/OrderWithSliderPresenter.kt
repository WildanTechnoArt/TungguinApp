package com.hyperdev.tungguin.presenter

import com.hyperdev.tungguin.model.orderlandingpage.OrderLandingPageResponse
import com.hyperdev.tungguin.model.profile.ProfileResponse
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.ui.view.OrderWithSliderView
import com.hyperdev.tungguin.utils.SchedulerProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subscribers.ResourceSubscriber

class OrderWithSliderPresenter(
    private val view: OrderWithSliderView.View,
    private val baseApiService: BaseApiService,
    private val scheduler: SchedulerProvider
) : OrderWithSliderView.Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun getOrderWithSlider(token: String) {
        view.displayProgress()

        compositeDisposable.add(
            baseApiService.getOrderWithSlider(token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(scheduler.io())
                .subscribeWith(object : ResourceSubscriber<OrderLandingPageResponse>() {
                    override fun onComplete() {
                        view.hideProgress()
                    }

                    override fun onNext(t: OrderLandingPageResponse) {
                        try {
                            t.data?.let { it.sliderImage?.let { it1 -> view.displayOrderWithSlider(it1) } }
                            t.data?.let { view.displayOnlineDesigner(it) }
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

    override fun getUserProfile(token: String) {
        compositeDisposable.add(
            baseApiService.getProfile(token, "application/json")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(scheduler.io())
                .subscribeWith(object : ResourceSubscriber<ProfileResponse>() {
                    override fun onComplete() {}

                    override fun onNext(t: ProfileResponse) {
                        try {
                            t.data?.let { view.displayProfile(it) }
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