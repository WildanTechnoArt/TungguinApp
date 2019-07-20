package com.hyperdev.tungguin.presenter

import android.content.Context
import android.widget.Toast
import com.google.gson.Gson
import com.hyperdev.tungguin.model.profile.ProfileResponse
import com.hyperdev.tungguin.model.transaction.TopUpResponse
import com.hyperdev.tungguin.network.ConnectivityStatus
import com.hyperdev.tungguin.network.Response
import com.hyperdev.tungguin.repository.profile.ProfileRepositoryImpl
import com.hyperdev.tungguin.repository.transaction.TransactionRepositoryImp
import com.hyperdev.tungguin.utils.SchedulerProvider
import com.hyperdev.tungguin.ui.view.TopUpView
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subscribers.ResourceSubscriber
import retrofit2.HttpException
import java.net.SocketTimeoutException

class TopUpPresenter(
    private val view: TopUpView.View,
    private val context: Context,
    private val profile: ProfileRepositoryImpl,
    private val topup: TransactionRepositoryImp,
    private val scheduler: SchedulerProvider
) : TopUpView.Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun topUpMoney(token: String, accept: String, amount: String) {
        view.showProgressBar()

        topup.topUpMoney(token, accept, amount)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(scheduler.io())
            .unsubscribeOn(scheduler.io())
            .subscribe(object : Observer<TopUpResponse> {
                override fun onComplete() {
                    view.onSuccess()
                }

                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onNext(t: TopUpResponse) {
                    t.getData?.let { view.transactionData(it) }
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
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

    override fun getUserAmount(token: String) {
        view.showProgressBar()

        compositeDisposable.add(
            profile.getProfile(token, "application/json")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(scheduler.io())
                .subscribeWith(object : ResourceSubscriber<ProfileResponse>() {
                    override fun onComplete() {
                        view.hideProgressBar()
                    }

                    override fun onNext(t: ProfileResponse) {
                        try {
                            t.data?.let { view.displayAmount(it) }
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
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
        )
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
    }
}