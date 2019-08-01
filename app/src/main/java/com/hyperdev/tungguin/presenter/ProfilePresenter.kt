package com.hyperdev.tungguin.presenter

import com.hyperdev.tungguin.model.authentication.CityResponse
import com.hyperdev.tungguin.model.authentication.ProvinceResponse
import com.hyperdev.tungguin.model.profile.ProfileResponse
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.ui.view.ProfileView
import com.hyperdev.tungguin.utils.SchedulerProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subscribers.ResourceSubscriber

class ProfilePresenter(
    private val view: ProfileView.View,
    private val baseApiService: BaseApiService,
    private val scheduler: SchedulerProvider
) : ProfileView.Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun getUserProfile(token: String) {
        view.displayProgress()

        compositeDisposable.add(
            baseApiService.getProfile(token, "application/json")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(scheduler.io())
                .subscribeWith(object : ResourceSubscriber<ProfileResponse>() {
                    override fun onComplete() {
                        view.hideProgress()
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
                        view.hideProgress()
                    }
                })
        )
    }

    override fun getCityAll(id: String) {
        compositeDisposable.add(
            baseApiService.getAllCity(id)
                .observeOn(scheduler.ui())
                .subscribeOn(scheduler.io())
                .subscribeWith(object : ResourceSubscriber<CityResponse>() {
                    override fun onComplete() {}

                    override fun onNext(t: CityResponse) {
                        try {
                            t.cityList?.let { view.displayCity(it) }
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }
                    }

                    override fun onError(e: Throwable) {
                        view.hideProgress()
                    }
                })
        )
    }

    override fun getProvinceAll() {
        view.displayProgress()

        compositeDisposable.add(
            baseApiService.getAllProvince()
                .observeOn(scheduler.ui())
                .subscribeOn(scheduler.io())
                .subscribeWith(object : ResourceSubscriber<ProvinceResponse>() {
                    override fun onComplete() {
                        view.hideProgress()
                    }

                    override fun onNext(t: ProvinceResponse) {
                        try {
                            t.provinceList?.let { view.displayProvince(it) }
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }
                    }

                    override fun onError(e: Throwable) {
                        view.hideProgress()
                    }
                })
        )
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
    }
}