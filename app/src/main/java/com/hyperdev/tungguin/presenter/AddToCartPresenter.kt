package com.hyperdev.tungguin.presenter

import android.content.Context
import android.widget.Toast
import com.google.gson.Gson
import com.hyperdev.tungguin.model.cart.AddToCartResponse
import com.hyperdev.tungguin.network.ConnectivityStatus
import com.hyperdev.tungguin.network.Response
import com.hyperdev.tungguin.repository.cart.CartRepositoryImp
import com.hyperdev.tungguin.ui.view.AddToCartView
import com.hyperdev.tungguin.utils.SchedulerProvider
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.net.SocketTimeoutException

class AddToCartPresenter(
    private val context: Context,
    private val view: AddToCartView.View,
    private val cart: CartRepositoryImp,
    private val scheduler: SchedulerProvider
) : AddToCartView.Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun addToCart(
        token: String, accept: String,
        cartMap: HashMap<String, RequestBody>,
        file: MutableList<MultipartBody.Part>,
        fileDoc: MultipartBody.Part?, price: RequestBody?
    ) {

        view.showProgressBar()

        cart.addToCart(token, accept, cartMap, file, fileDoc, price)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(scheduler.io())
            .unsubscribeOn(scheduler.io())
            .subscribe(object : Observer<AddToCartResponse> {
                override fun onComplete() {
                    view.onSuccess()
                }

                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onNext(t: AddToCartResponse) {}

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