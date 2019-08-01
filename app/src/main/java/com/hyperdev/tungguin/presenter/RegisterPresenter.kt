package com.hyperdev.tungguin.presenter

import android.content.Context
import com.hyperdev.tungguin.database.SharedPrefManager
import com.hyperdev.tungguin.model.authentication.CityResponse
import com.hyperdev.tungguin.model.authentication.ProvinceResponse
import com.hyperdev.tungguin.model.authentication.RegisterResponse
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.ui.view.RegisterView
import com.hyperdev.tungguin.utils.SchedulerProvider
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subscribers.ResourceSubscriber

class RegisterPresenter(
    private val view: RegisterView.View,
    private val baseApiService: BaseApiService,
    private val scheduler: SchedulerProvider
) : RegisterView.Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun postDataUser(register: HashMap<String, String>, context: Context) {
        view.displayProgress()
        baseApiService.registerRequest("application/json", register)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(scheduler.io())
            .unsubscribeOn(scheduler.io())
            .subscribe(object : Observer<RegisterResponse> {
                override fun onComplete() {
                    view.onSuccess()
                }

                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onNext(t: RegisterResponse) {
                    SharedPrefManager.getInstance(context).storeToken(t.getData?.token.toString())
                }

                override fun onError(e: Throwable) {
                    view.hideProgress()
                    view.handleError(e)
                }

            })
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
                        view.handleError(e)
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
                        view.handleError(e)
                        view.hideProgress()
                    }
                })
        )
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
    }
}