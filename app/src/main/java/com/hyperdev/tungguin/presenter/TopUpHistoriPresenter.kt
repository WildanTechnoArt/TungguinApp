package com.hyperdev.tungguin.presenter

import android.content.Context
import android.widget.Toast
import com.hyperdev.tungguin.model.topuphistori.HistoriTopUpResponse
import com.hyperdev.tungguin.network.ConnectivityStatus
import com.hyperdev.tungguin.network.HandleError
import com.hyperdev.tungguin.repository.TopUpHIstoryRepositoryImpl
import com.hyperdev.tungguin.utils.SchedulerProvider
import com.hyperdev.tungguin.view.HistoriTopUpView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subscribers.ResourceSubscriber
import retrofit2.HttpException
import java.net.SocketTimeoutException

class TopUpHistoriPresenter(private val view: HistoriTopUpView.View,
                            private val context: Context,
                            private val history: TopUpHIstoryRepositoryImpl,
                            private val scheduler: SchedulerProvider) : HistoriTopUpView.Presenter{

    private val compositeDisposable = CompositeDisposable()

    override fun getTopUpHistory(token: String, page: Int) {
        view.displayProgress()

        compositeDisposable.add(history.getTopUpHistory(token, "application/json", page)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(scheduler.io())
            .subscribeWith(object : ResourceSubscriber<HistoriTopUpResponse>(){
                override fun onComplete() {
                    view.onSuccess()
                }

                override fun onNext(t: HistoriTopUpResponse) {
                    try{
                        t.data?.let { it.listTopUp?.let { it1 -> view.showTopUpHistory(it1) } }
                        t.data?.let { view.showTopUp(it) }
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