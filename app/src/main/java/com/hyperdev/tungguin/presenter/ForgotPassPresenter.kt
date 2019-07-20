package com.hyperdev.tungguin.presenter

import android.content.Context
import android.widget.Toast
import com.google.gson.Gson
import com.hyperdev.tungguin.model.authentication.LoginResponse
import com.hyperdev.tungguin.network.ConnectivityStatus
import com.hyperdev.tungguin.network.Response
import com.hyperdev.tungguin.repository.other.ForgotPassRepositoryImpl
import com.hyperdev.tungguin.ui.view.ForgotPassView
import com.hyperdev.tungguin.utils.SchedulerProvider
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import retrofit2.HttpException
import java.net.SocketTimeoutException

class ForgotPassPresenter(
    private val context: Context,
    private val view: ForgotPassView.View,
    private val pass: ForgotPassRepositoryImpl,
    private val scheduler: SchedulerProvider
) : ForgotPassView.Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun forgotPassword(email: String) {
        view.showProgressBar()
        pass.forgotPassword("application/json", email)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(scheduler.io())
            .unsubscribeOn(scheduler.io())
            .subscribe(object : Observer<LoginResponse> {
                override fun onComplete() {
                    view.onSuccess()
                }

                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onNext(t: LoginResponse) {
                    val message = t.getMeta?.message
                    if (message != null) {
                        Toast.makeText(context, message.toString(), Toast.LENGTH_LONG).show()
                    }
                }

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