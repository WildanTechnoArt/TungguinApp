package com.hyperdev.tungguin.presenter

import android.content.Context
import android.widget.Toast
import com.hyperdev.tungguin.model.orderlandingpage.OrderLandingPageResponse
import com.hyperdev.tungguin.model.profile.ProfileResponse
import com.hyperdev.tungguin.network.ConnectivityStatus
import com.hyperdev.tungguin.network.HandleError
import com.hyperdev.tungguin.repository.OrderWithSliderRepositoryImpl
import com.hyperdev.tungguin.repository.ProfileRepositoryImpl
import com.hyperdev.tungguin.utils.SchedulerProvider
import com.hyperdev.tungguin.view.OrderWithSliderView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subscribers.ResourceSubscriber
import retrofit2.HttpException
import java.net.SocketTimeoutException

// Digunakan untuk menjembatani Model dengan View pada Fragment
class OrderWithSliderPresenter(private val view: OrderWithSliderView.View,
                               private val context: Context,
                               private val orderData: OrderWithSliderRepositoryImpl,
                               private val profile: ProfileRepositoryImpl,
                               private val scheduler: SchedulerProvider) : OrderWithSliderView.Presenter{

    private val compositeDisposable = CompositeDisposable()

    override fun getOrderWithSlider(token: String) {
        view.displayProgress()
        compositeDisposable.add(orderData.getOrderWithSlider(token)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(scheduler.io())
            .subscribeWith(object : ResourceSubscriber<OrderLandingPageResponse>(){
                override fun onComplete() {
                    view.hideProgress()
                }

                override fun onNext(t: OrderLandingPageResponse) {
                    try{
                        t.data?.let { it.sliderImage?.let { it1 -> view.displayOrderWithSlider(it1) } }
                        t.data?.let { view.displayOnlineDesigner(it) }
                    }catch(ex: Exception){
                        ex.printStackTrace()
                    }
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }
            })
        )
    }

    override fun getUserProfile(token: String) {
        compositeDisposable.add(profile.getProfile(token, "application/json")
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(scheduler.io())
            .subscribeWith(object : ResourceSubscriber<ProfileResponse>(){
                override fun onComplete() {}

                override fun onNext(t: ProfileResponse) {
                    try{
                        t.data?.let { view.displayProfile(it) }
                    }catch(ex: Exception){
                        ex.printStackTrace()
                    }
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    view.hideProgress()

                    if(ConnectivityStatus.isConnected(context)){
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