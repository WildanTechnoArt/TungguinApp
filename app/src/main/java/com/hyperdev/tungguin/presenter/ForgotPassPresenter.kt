package com.hyperdev.tungguin.presenter

import android.content.Context
import android.widget.Toast
import com.hyperdev.tungguin.model.authentication.LoginResponse
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.ui.view.ForgotPassView
import com.hyperdev.tungguin.utils.SchedulerProvider
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class ForgotPassPresenter(
    private val view: ForgotPassView.View,
    private val baseApiService: BaseApiService,
    private val scheduler: SchedulerProvider
) : ForgotPassView.Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun forgotPassword(email: String, context: Context) {
        view.showProgressBar()
        baseApiService.forgotPassword("application/json", email)
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
                    view.handleError(e)
                }
            })
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
    }
}