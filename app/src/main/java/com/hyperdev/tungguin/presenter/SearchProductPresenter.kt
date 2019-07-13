package com.hyperdev.tungguin.presenter

import android.content.Context
import android.widget.Toast
import com.hyperdev.tungguin.model.searchproduct.SearchProductResponse
import com.hyperdev.tungguin.network.ConnectivityStatus
import com.hyperdev.tungguin.network.HandleError
import com.hyperdev.tungguin.repository.product.ProductRepositoryImpl
import com.hyperdev.tungguin.utils.SchedulerProvider
import com.hyperdev.tungguin.ui.view.SearchProductView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subscribers.ResourceSubscriber
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.util.*

class SearchProductPresenter(
    private val view: SearchProductView.View,
    private val context: Context,
    private val product: ProductRepositoryImpl?,
    private val scheduler: SchedulerProvider
) : SearchProductView.Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun getProductByName(token: String, name: String?) {
        view.showSearchProgressBar()

        product?.searchProuctByName(token, "application/json", name.toString())
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
                    e.printStackTrace()
                    view.hideSearchProgressBar()
                    view.showProductByName(Collections.emptyList())

                    if (ConnectivityStatus.isConnected(context)) {
                        when (e) {
                            is HttpException -> // non 200 error codes
                                HandleError.handleError(e, e.code(), context)
                            is SocketTimeoutException -> // connection errors
                                Toast.makeText(context, "Connection Timeout!", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(context, "Tidak Terhubung Dengan Internet!", Toast.LENGTH_LONG).show()
                    }
                }
            })?.let {
                compositeDisposable.add(
                    it
                )
            }
    }

    override fun getProductByPage(token: String, page: Int?) {
        view.showSearchProgressBar()

        product?.searchProuctByPage(token, "application/json", page!!)
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
                    e.printStackTrace()
                    view.hideSearchProgressBar()
                    view.showProductByPage(Collections.emptyList())

                    if (ConnectivityStatus.isConnected(context)) {
                        when (e) {
                            is HttpException -> // non 200 error codes
                                HandleError.handleError(e, e.code(), context)
                            is SocketTimeoutException -> // connection errors
                                Toast.makeText(context, "Connection Timeout!", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(context, "Tidak Terhubung Dengan Internet!", Toast.LENGTH_LONG).show()
                    }
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