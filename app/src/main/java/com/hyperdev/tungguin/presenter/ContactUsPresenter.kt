package com.hyperdev.tungguin.presenter

import com.hyperdev.tungguin.model.contact.ContactUsResponse
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.ui.view.ContactUsView
import com.hyperdev.tungguin.utils.SchedulerProvider
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class ContactUsPresenter(
    private val view: ContactUsView.View,
    private val baseApiService: BaseApiService,
    private val scheduler: SchedulerProvider
) : ContactUsView.Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun contactUs(token: String, accept: String, title: String, content: String) {
        view.showProgressBar()

        baseApiService.contactUs(token, accept, title, content)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(scheduler.io())
            .unsubscribeOn(scheduler.io())
            .subscribe(object : Observer<ContactUsResponse> {
                override fun onComplete() {
                    view.onSuccess()
                }

                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onNext(t: ContactUsResponse) {
                    t.meta?.let { view.contactUsMessage(it) }
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