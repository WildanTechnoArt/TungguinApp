package com.hyperdev.tungguin.presenter

import com.hyperdev.tungguin.model.searchproduct.SearchProductResponse
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.ui.view.SearchProductView
import com.hyperdev.tungguin.utils.SchedulerProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subscribers.ResourceSubscriber
import java.util.*

class SearchProductPresenter(
    private val view: SearchProductView.View,
    private val baseApiService: BaseApiService?,
    private val scheduler: SchedulerProvider
) : SearchProductView.Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun getProductByName(token: String, name: String?) {
        view.showSearchProgressBar()

        baseApiService?.searchProuctByName(token, "application/json", name.toString())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribeOn(scheduler.io())
            ?.subscribeWith(object : ResourceSubscriber<SearchProductResponse>() {
                override fun onComplete() {
                    view.hideSearchProgressBar()
                }

                override fun onNext(t: SearchProductResponse) {
                    try {
                        t.data?.let { it.dataProduct?.let { it1 -> view.showProductByName(it1) } }
                        t.data?.let { view.getProductPageUrl(it) }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }

                override fun onError(e: Throwable) {
                    view.hideSearchProgressBar()
                    view.showProductByName(Collections.emptyList())
                    view.handleError(e)
                }
            })?.let {
                compositeDisposable.add(
                    it
                )
            }
    }

    override fun getProductByPage(token: String, page: Int?) {
        view.showSearchProgressBar()

        baseApiService?.searchProuctByPage(token, "application/json", page!!)
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribeOn(scheduler.io())
            ?.subscribeWith(object : ResourceSubscriber<SearchProductResponse>() {
                override fun onComplete() {
                    view.hideSearchProgressBar()
                }

                override fun onNext(t: SearchProductResponse) {
                    try {
                        t.data?.let { it.dataProduct?.let { it1 -> view.showProductByPage(it1) } }
                        t.data?.let { view.getProductPageUrl(it) }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }

                override fun onError(e: Throwable) {
                    view.hideSearchProgressBar()
                    view.showProductByPage(Collections.emptyList())
                    view.handleError(e)
                }
            })?.let {
                compositeDisposable.add(
                    it
                )
            }
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
    }
}