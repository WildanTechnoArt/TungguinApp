package com.hyperdev.tungguin.presenter

import android.content.Context
import com.hyperdev.tungguin.database.SharedPrefManager
import com.hyperdev.tungguin.model.authentication.LoginResponse
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.ui.view.LoginView
import com.hyperdev.tungguin.utils.SchedulerProvider
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class LoginPresenter(
    private val view: LoginView.View,
    private val baseApiService: BaseApiService,
    private val scheduler: SchedulerProvider
) : LoginView.Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun loginUser(email: String, password: String, context: Context) {
        view.showProgressBar()
        baseApiService.loginRequest("application/json", email, password)
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
                    SharedPrefManager.getInstance(context).storeToken(t.getData?.token.toString())
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