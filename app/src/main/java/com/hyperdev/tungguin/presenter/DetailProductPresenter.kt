package com.hyperdev.tungguin.presenter

import com.hyperdev.tungguin.model.detailproduct.DetailProductResponse
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.ui.view.DetailProductView
import com.hyperdev.tungguin.utils.SchedulerProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subscribers.ResourceSubscriber

class DetailProductPresenter(
    private val view: DetailProductView.View,
    private val baseApiService: BaseApiService,
    private val scheduler: SchedulerProvider
) : DetailProductView.Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun getDetailProduct(token: String, hashed_id: String) {
        view.displayProgress()

        compositeDisposable.add(
            baseApiService.getDetailProduct(token, "application/json", hashed_id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(scheduler.io())
                .subscribeWith(object : ResourceSubscriber<DetailProductResponse>() {
                    override fun onComplete() {
                        view.hideProgress()
                    }

                    override fun onNext(t: DetailProductResponse) {
                        try {
                            t.data?.let { view.showDetailProductItem(it) }
                            t.data?.fieldListFormatted?.let { view.showFieldList(it) }
                            t.data?.priceList?.let { view.showPriceList(it) }
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }
                    }

                    override fun onError(e: Throwable) {
                        view.hideProgress()
                        view.handleError(e)
                    }
                })
        )
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
    }
}