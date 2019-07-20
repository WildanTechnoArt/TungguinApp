package com.hyperdev.tungguin.presenter

import android.content.Context
import android.widget.Toast
import com.google.gson.Gson
import com.hyperdev.tungguin.model.cart.CartResponse
import com.hyperdev.tungguin.model.cart.CheckVoucherResponse
import com.hyperdev.tungguin.model.cart.CheckoutResponse
import com.hyperdev.tungguin.network.ConnectivityStatus.Companion.isConnected
import com.hyperdev.tungguin.network.HandleError
import com.hyperdev.tungguin.network.Response
import com.hyperdev.tungguin.repository.cart.CartRepositoryImp
import com.hyperdev.tungguin.utils.SchedulerProvider
import com.hyperdev.tungguin.ui.view.MyCartView
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subscribers.ResourceSubscriber
import retrofit2.HttpException
import java.net.SocketTimeoutException

class CartPresenter(
    private val view: MyCartView.View,
    private val context: Context,
    private val cart: CartRepositoryImp,
    private val scheduler: SchedulerProvider
) : MyCartView.Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun getCartData(token: String) {
        view.showProgressBar()

        compositeDisposable.add(
            cart.getCartItem(token, "application/json")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(scheduler.io())
                .subscribeWith(object : ResourceSubscriber<CartResponse>() {
                    override fun onComplete() {
                        view.onSuccessLoadData()
                    }

                    override fun onNext(t: CartResponse) {
                        try {
                            t.data?.let { view.showCartData(it) }
                            t.data?.items?.let { view.showCartItem(it) }
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        view.hideProgressBar()

                        if (isConnected(context)) {
                            when (e) {
                                is HttpException -> // non 200 error codes
                                    HandleError.handleError(e, e.code(), context)
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

    override fun checkVoucher(token: String, accept: String, code: String) {
        view.showProgressBar()

        cart.checkVoucher(token, accept, code)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(scheduler.io())
            .unsubscribeOn(scheduler.io())
            .subscribe(object : Observer<CheckVoucherResponse> {
                override fun onComplete() {
                    view.onSuccessCheckVoucher()
                }

                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onNext(t: CheckVoucherResponse) {
                    t.kuponData?.let { view.getVoucherData(it) }
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    view.hideProgressBar()

                    if (isConnected(context)) {
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

    override fun checkout(token: String, accept: String, paymentType: String, voucher: String) {
        view.showProgressBar()

        cart.checkout(token, accept, paymentType, voucher)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(scheduler.io())
            .unsubscribeOn(scheduler.io())
            .subscribe(object : Observer<CheckoutResponse> {
                override fun onComplete() {
                    view.onSuccessCheckout()
                }

                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onNext(t: CheckoutResponse) {
                    t.checkoutItem?.let { view.getCheckoutData(it) }
                }

                override fun onError(e: Throwable) {
                    view.hideProgressBar()

                    if (isConnected(context)) {
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