package com.hyperdev.tungguin.presenter

import android.content.Context
import android.widget.Toast
import com.hyperdev.tungguin.model.dashboard.AnnouncementResponse
import com.hyperdev.tungguin.model.dashboard.DashboardResponse
import com.hyperdev.tungguin.model.dashboard.FCMResponse
import com.hyperdev.tungguin.model.profile.ProfileResponse
import com.hyperdev.tungguin.network.ConnectivityStatus
import com.hyperdev.tungguin.network.HandleError
import com.hyperdev.tungguin.repository.dashboard.DashboardRepositoryImp
import com.hyperdev.tungguin.utils.SchedulerProvider
import com.hyperdev.tungguin.ui.view.DashboardView
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subscribers.ResourceSubscriber
import retrofit2.HttpException
import java.net.SocketTimeoutException

class DashboardPresenter(
    private val view: DashboardView.View,
    private val context: Context,
    private val dashboard: DashboardRepositoryImp,
    private val scheduler: SchedulerProvider
) : DashboardView.Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun sendTokenFcm(token: String, accept: String, tokenFcm: String?) {
        dashboard.postTokenFcm(token, accept, tokenFcm)
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
                    e.printStackTrace()
                }

            })
    }

    override fun resetTokenFcm(token: String, accept: String, tokenFcm: String?) {
        view.showProgressBar()

        dashboard.postTokenFcm(token, accept, tokenFcm)
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
                    e.printStackTrace()
                    view.hideProgressBar()
                }

            })
    }

    override fun getSliderImage(token: String) {
        compositeDisposable.add(
            dashboard.getImageSlider(token)
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
                        e.printStackTrace()
                    }
                })
        )
    }

    override fun getUserProfile(token: String) {
        view.showProgressBar()

        compositeDisposable.add(
            dashboard.getProfile(token, "application/json")
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
                        e.printStackTrace()
                        view.hideProgressBar()

                        if (ConnectivityStatus.isConnected(context)) {
                            when (e) {
                                is HttpException -> // non 200 error codes
                                    HandleError.handleError(e, e.code(), context)
                                is SocketTimeoutException -> // connection errors
                                    Toast.makeText(context, "Connection Timeout!", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            Toast.makeText(context, "Tidak Terhubung Dengan Internet!", Toast.LENGTH_LONG).show()
                        }
                    }
                })
        )
    }

    override fun getAnnouncementData(token: String) {
        compositeDisposable.add(
            dashboard.announcementDesigner(token)
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
                        e.printStackTrace()
                    }
                })
        )
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
    }
}