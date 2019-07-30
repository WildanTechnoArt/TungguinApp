package com.hyperdev.tungguin.presenter

import android.content.Context
import android.widget.Toast
import com.google.gson.Gson
import com.hyperdev.tungguin.model.profile.ProfileResponse
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.network.ConnectivityStatus
import com.hyperdev.tungguin.network.Response
import com.hyperdev.tungguin.utils.SchedulerProvider
import com.hyperdev.tungguin.ui.view.ProfileUpdateView
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import retrofit2.HttpException
import java.net.SocketTimeoutException

class ProfileUpdatePresenter(
    private val context: Context,
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

    override fun onDestroy() {
        compositeDisposable.dispose()
    }
}