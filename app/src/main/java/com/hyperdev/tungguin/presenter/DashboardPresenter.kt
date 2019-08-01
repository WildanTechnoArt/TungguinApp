package com.hyperdev.tungguin.presenter

import com.hyperdev.tungguin.model.dashboard.AnnouncementResponse
import com.hyperdev.tungguin.model.dashboard.DashboardResponse
import com.hyperdev.tungguin.model.dashboard.FCMResponse
import com.hyperdev.tungguin.model.profile.ProfileResponse
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.ui.view.DashboardView
import com.hyperdev.tungguin.utils.SchedulerProvider
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subscribers.ResourceSubscriber

class DashboardPresenter(
    private val view: DashboardView.View,
    private val baseApiService: BaseApiService,
    private val scheduler: SchedulerProvider
) : DashboardView.Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun sendTokenFcm(token: String, accept: String, tokenFcm: String?) {
        baseApiService.fcmRequest(token, accept, tokenFcm)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(scheduler.io())
            .unsubscribeOn(scheduler.io())
            .subscribe(object : Observer<FCMResponse> {
                override fun onComplete() {}

                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onNext(t: FCMResponse) {}

                override fun onError(e: Throwable) {
                    view.handleError(e)
                }

            })
    }

    override fun resetTokenFcm(token: String, accept: String, tokenFcm: String?) {
        view.showProgressBar()

        baseApiService.fcmRequest(token, accept, tokenFcm)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(scheduler.io())
            .unsubscribeOn(scheduler.io())
            .subscribe(object : Observer<FCMResponse> {
                override fun onComplete() {
                    view.onSuccessResetToken()
                }

                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onNext(t: FCMResponse) {}

                override fun onError(e: Throwable) {
                    view.hideProgressBar()
                    view.handleError(e)
                }
            })
    }

    override fun getSliderImage(token: String) {
        compositeDisposable.add(
            baseApiService.getDashboardSlider(token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(scheduler.io())
                .subscribeWith(object : ResourceSubscriber<DashboardResponse>() {
                    override fun onComplete() {}

                    override fun onNext(t: DashboardResponse) {
                        try {
                            t.data?.let { view.showSliderImage(it) }
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
        view.showProgressBar()

        compositeDisposable.add(
            baseApiService.getProfile(token, "application/json")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(scheduler.io())
                .subscribeWith(object : ResourceSubscriber<ProfileResponse>() {
                    override fun onComplete() {
                        view.hideProgressBar()
                    }

                    override fun onNext(t: ProfileResponse) {
                        try {
                            t.data?.let { view.showProfile(it) }
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

    override fun getAnnouncementData(token: String) {
        compositeDisposable.add(
            baseApiService.announcementDesigner(token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(scheduler.io())
                .subscribeWith(object : ResourceSubscriber<AnnouncementResponse>() {
                    override fun onComplete() {}

                    override fun onNext(t: AnnouncementResponse) {
                        try {
                            t.data?.let { view.showAnnouncement(it) }
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

    override fun onDestroy() {
        compositeDisposable.dispose()
    }
}