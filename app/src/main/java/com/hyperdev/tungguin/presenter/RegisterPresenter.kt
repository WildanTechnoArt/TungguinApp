package com.hyperdev.tungguin.presenter

import android.content.Context
import android.widget.Toast
import com.google.gson.Gson
import com.hyperdev.tungguin.database.SharedPrefManager
import com.hyperdev.tungguin.model.authentication.CityResponse
import com.hyperdev.tungguin.model.authentication.ProvinceResponse
import com.hyperdev.tungguin.model.authentication.RegisterResponse
import com.hyperdev.tungguin.network.ConnectivityStatus
import com.hyperdev.tungguin.network.Response
import com.hyperdev.tungguin.repository.authentication.AuthRepositoryImp
import com.hyperdev.tungguin.utils.SchedulerProvider
import com.hyperdev.tungguin.ui.view.RegisterView
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subscribers.ResourceSubscriber
import retrofit2.HttpException
import java.net.SocketTimeoutException

class RegisterPresenter(
    private val context: Context,
    private val view: RegisterView.View,
    private val reg: AuthRepositoryImp,
    private val scheduler: SchedulerProvider
) : RegisterView.Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun postDataUser(register: HashMap<String, String>) {
        view.displayProgress()
        reg.registerRequest("application/json", register)
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

                    if (ConnectivityStatus.isConnected(context)) {
                        when (e) {
                            is HttpException -> {
                                val gson = Gson()
                                val response =
                                    gson.fromJson(e.response()?.errorBody()?.charStream(), Response::class.java)
                                val message = response.meta?.message.toString()
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                            }
                            is SocketTimeoutException -> // connection errors
                                view.noInternetConnection("Connection Timeout!")
                        }
                    } else {
                        view.noInternetConnection("Tidak Terhubung Dengan Internet!")
                    }
                }

            })
    }

    override fun getProvinceAll() {
        view.displayProgress()

        compositeDisposable.add(
            reg.getProvince()
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
                        e.printStackTrace()
                        view.hideProgress()
                    }
                })
        )
    }

    override fun getCityAll(id: String) {
        compositeDisposable.add(
            reg.getCity(id)
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
                        e.printStackTrace()
                    }
                })
        )
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
    }
}