package com.hyperdev.tungguin.presenter

import android.content.Context
import android.widget.Toast
import com.hyperdev.tungguin.model.detailorder.DetailOrderResponse
import com.hyperdev.tungguin.network.ConnectivityStatus
import com.hyperdev.tungguin.network.HandleError
import com.hyperdev.tungguin.repository.DetailOrderRepositoryImpl
import com.hyperdev.tungguin.utils.SchedulerProvider
import com.hyperdev.tungguin.view.SearchDesignerView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subscribers.ResourceSubscriber
import retrofit2.HttpException
import java.net.SocketTimeoutException

class SearchDesignPresenter(private val view: SearchDesignerView.View,
                            private val context: Context,
                            private val detail: DetailOrderRepositoryImpl,
                            private val scheduler: SchedulerProvider) : SearchDesignerView.Presenter{

    private val compositeDisposable = CompositeDisposable()

    override fun getDetailOrder(token: String, id: String) {
        view.displayProgress()

        compositeDisposable.add(detail.getOrderDetail(token, "application/json", id)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(scheduler.io())
            .subscribeWith(object : ResourceSubscriber<DetailOrderResponse>(){
                override fun onComplete() {
                    view.hideProgress()
                    view.onSuccess()
                }

                override fun onNext(t: DetailOrderResponse) {
                    try{
                        view.showDetailOrder(t.data)
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