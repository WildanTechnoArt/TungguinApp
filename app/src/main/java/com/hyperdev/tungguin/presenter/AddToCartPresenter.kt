package com.hyperdev.tungguin.presenter

import com.hyperdev.tungguin.model.cart.AddToCartResponse
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.ui.view.AddToCartView
import com.hyperdev.tungguin.utils.SchedulerProvider
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddToCartPresenter(
    private val view: AddToCartView.View,
    private val baseApiService: BaseApiService,
    private val scheduler: SchedulerProvider
) : AddToCartView.Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun addToCart(
        token: String, accept: String,
        cartMap: HashMap<String, RequestBody>,
        file: MutableList<MultipartBody.Part>,
        fileDoc: MultipartBody.Part?, price: RequestBody?
    ) {

        view.showProgressBar()

        baseApiService.addToCart(token, accept, cartMap, file, fileDoc, price)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(scheduler.io())
            .unsubscribeOn(scheduler.io())
            .subscribe(object : Observer<AddToCartResponse> {
                override fun onComplete() {
                    view.onSuccess()
                }

                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onNext(t: AddToCartResponse) {}

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