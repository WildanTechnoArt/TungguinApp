package com.hyperdev.tungguin.presenter

import com.hyperdev.tungguin.model.profile.ProfileResponse
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.ui.view.ProfileUpdateView
import com.hyperdev.tungguin.utils.SchedulerProvider
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class ProfileUpdatePresenter(
    private val view: ProfileUpdateView.View,
    private val baseApiService: BaseApiService,
    private val scheduler: SchedulerProvider
) : ProfileUpdateView.Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun updateProfile(
        token: String, accept: String,
        name: String, email: String,
        phone: String, province: String, city: String
    ) {
        view.showProgressBar()
        baseApiService.updateProfile(token, accept, name, email, phone, province, city)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(scheduler.io())
            .unsubscribeOn(scheduler.io())
            .subscribe(object : Observer<ProfileResponse> {
                override fun onComplete() {
                    view.onSuccess()
                }

                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onNext(t: ProfileResponse) {}

                override fun onError(e: Throwable) {
                    view.hideProgressBar()
                    view.handleError(e)
                }

            })
    }

    override fun updatePassword(
        token: String, accept: String, name: String,
        email: String, phone: String, province: String,
        city: String, password: String, c_password: String
    ) {
        view.showProgressBar()
        baseApiService.updatePassword(token, accept, name, email, phone, province, city, password, c_password)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(scheduler.io())
            .unsubscribeOn(scheduler.io())
            .subscribe(object : Observer<ProfileResponse> {
                override fun onComplete() {
                    view.onSuccess()
                }

                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onNext(t: ProfileResponse) {}

                override fun onError(e: Throwable) {
                    view.hideProgressBar()
                    view.handleError(e)
                }

            })
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
    }
}