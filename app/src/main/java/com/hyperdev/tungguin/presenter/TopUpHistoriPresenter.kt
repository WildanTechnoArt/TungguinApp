package com.hyperdev.tungguin.presenter

import android.content.Context
import com.hyperdev.tungguin.model.topuphistori.HistoriTopUpResponse
import com.hyperdev.tungguin.repository.TopUpHIstoryRepositoryImpl
import com.hyperdev.tungguin.utils.SchedulerProvider
import com.hyperdev.tungguin.view.HistoriTopUpView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subscribers.ResourceSubscriber

//Digunakan untuk menjembatani Model dengan View pada Fragment
class TopUpHistoriPresenter(private val view: HistoriTopUpView.View,
                            private val history: TopUpHIstoryRepositoryImpl,
                            private val scheduler: SchedulerProvider) : HistoriTopUpView.Presenter{

    private val compositeDisposable = CompositeDisposable()

    override fun getTopUpHistory(context: Context, token: String, page: Int) {
        view.displayProgress()

        compositeDisposable.add(history.getTopUpHistory(token, page)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(scheduler.io())
            .subscribeWith(object : ResourceSubscriber<HistoriTopUpResponse>(){
                override fun onComplete() {
                    view.hideProgress()
                }

                override fun onNext(t: HistoriTopUpResponse) {
                    try{
                        t.data?.let { it.listTopUp?.let { it1 -> view.showTopUpHistory(it1) } }
                    }catch(ex: Exception){
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

    override fun onDestroy() {
        compositeDisposable.dispose()
    }
}