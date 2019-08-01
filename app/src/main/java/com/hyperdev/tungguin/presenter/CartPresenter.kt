package com.hyperdev.tungguin.presenter

import com.hyperdev.tungguin.model.cart.CartResponse
import com.hyperdev.tungguin.model.cart.CheckVoucherResponse
import com.hyperdev.tungguin.model.cart.CheckoutResponse
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.ui.view.MyCartView
import com.hyperdev.tungguin.utils.SchedulerProvider
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subscribers.ResourceSubscriber

class CartPresenter(
    private val view: MyCartView.View,
    private val baseApiService: BaseApiService,
    private val scheduler: SchedulerProvider
) : MyCartView.Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun getCartData(token: String) {
        view.showProgressBar()

        compositeDisposable.add(
            baseApiService.getCartData(token, "application/json")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(scheduler.io())
                .subscribeWith(object : ResourceSubscriber<CartResponse>() {
                    override fun onComplete() {
                        view.onSuccessLoadData()
                    }

                    override fun onNext(t: CartResponse) {
                        try {
                            t.data?.let { view.showCartData(it) }
                            t.data?.items?.let { view.showCartItem(it) }
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

    override fun checkVoucher(token: String, accept: String, code: String) {
        view.showProgressBar()

        baseApiService.checkKupon(token, accept, code)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(scheduler.io())
            .unsubscribeOn(scheduler.io())
            .subscribe(object : Observer<CheckVoucherResponse> {
                override fun onComplete() {
                    view.onSuccessCheckVoucher()
                }

                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onNext(t: CheckVoucherResponse) {
                    t.kuponData?.let { view.getVoucherData(it) }
                }

                override fun onError(e: Throwable) {
                    view.hideProgressBar()
                    view.handleError(e)
                }

            })
    }

    override fun checkout(token: String, accept: String, paymentType: String, voucher: String) {
        view.showProgressBar()

        baseApiService.checkout(token, accept, paymentType, voucher)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(scheduler.io())
            .unsubscribeOn(scheduler.io())
            .subscribe(object : Observer<CheckoutResponse> {
                override fun onComplete() {
                    view.onSuccessCheckout()
                }

                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onNext(t: CheckoutResponse) {
                    t.checkoutItem?.let { view.getCheckoutData(it) }
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