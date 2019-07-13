package com.hyperdev.tungguin.presenter

import android.content.Context
import android.widget.Toast
import com.hyperdev.tungguin.model.detailproduct.DetailProductResponse
import com.hyperdev.tungguin.network.ConnectivityStatus
import com.hyperdev.tungguin.network.HandleError
import com.hyperdev.tungguin.repository.product.ProductRepositoryImpl
import com.hyperdev.tungguin.utils.SchedulerProvider
import com.hyperdev.tungguin.ui.view.DetailProductView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subscribers.ResourceSubscriber
import retrofit2.HttpException
import java.net.SocketTimeoutException

class DetailProductPresenter(
    private val view: DetailProductView.View,
    private val context: Context,
    private val detail: ProductRepositoryImpl,
    private val scheduler: SchedulerProvider
) : DetailProductView.Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun getDetailProduct(token: String, hashed_id: String) {
        view.displayProgress()

        compositeDisposable.add(
            detail.getDetailProduct(token, "application/json", hashed_id)
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
                        e.printStackTrace()
                        view.hideProgress()

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
                })
        )
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
    }
}