package com.hyperdev.tungguin.presenter

import android.content.Context
import android.widget.Toast
import com.hyperdev.tungguin.model.cart.CartResponse
import com.hyperdev.tungguin.network.ConnectivityStatus.Companion.isConnected
import com.hyperdev.tungguin.network.HandleError
import com.hyperdev.tungguin.repository.CartRepositoryImpl
import com.hyperdev.tungguin.utils.SchedulerProvider
import com.hyperdev.tungguin.view.CartView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subscribers.ResourceSubscriber
import retrofit2.HttpException
import java.net.SocketTimeoutException

class CartPresenter(private val view: CartView.View,
                    private val context: Context,
                    private val cart: CartRepositoryImpl,
                    private val scheduler: SchedulerProvider) : CartView.Presenter{

    private val compositeDisposable = CompositeDisposable()

    override fun getCartData(token: String) {
        view.displayProgress()

        compositeDisposable.add(cart.getCartData(token, "application/json")
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(scheduler.io())
            .subscribeWith(object : ResourceSubscriber<CartResponse>(){
                override fun onComplete() {
                    view.hideProgress()
                }

                override fun onNext(t: CartResponse) {
                    try{
                        t.data?.let { view.showCartData(it) }
                        t.data?.items?.let { view.showCartItem(it) }
                    }catch(ex: Exception){
                        ex.printStackTrace()
                    }
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    view.hideProgress()

                    if(isConnected(context)){
                        when (e) {
                            is HttpException -> // non 200 error codes
                                HandleError.handleError(e, e.code(), context)
                            is SocketTimeoutException -> // connection errors
                                Toast.makeText(context, "Connection Timeout!", Toast.LENGTH_LONG).show()
                        }
                    }else{
                        Toast.makeText(context, "Tidak Terhubung Dengan Internet!", Toast.LENGTH_LONG).show()
                    }
                }
            })
        )
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
    }
}