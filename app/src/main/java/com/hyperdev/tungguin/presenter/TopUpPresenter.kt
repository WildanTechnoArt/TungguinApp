package com.hyperdev.tungguin.presenter

import com.hyperdev.tungguin.model.profile.ProfileResponse
import com.hyperdev.tungguin.model.transaction.TopUpResponse
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.ui.view.TopUpView
import com.hyperdev.tungguin.utils.SchedulerProvider
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subscribers.ResourceSubscriber

class TopUpPresenter(
    private val view: TopUpView.View,
    private val baseApiService: BaseApiService,
    private val scheduler: SchedulerProvider
) : TopUpView.Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun topUpMoney(token: String, accept: String, amount: String) {
        view.showProgressBar()

        baseApiService.topUpMoney(token, accept, amount)
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
                    view.hideProgressBar()
                    view.handleError(e)
                }
            })
    }

    override fun getUserAmount(token: String) {
        view.showProgressBar()

        compositeDisposable.add(
            baseApiService.getProfile(token, "application/json")
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
                        view.hideProgressBar()
                        view.handleError(e)
                    }
                })
        )
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
    }
}