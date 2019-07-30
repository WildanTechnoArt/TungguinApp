package com.hyperdev.tungguin.presenter

import android.content.Context
import android.widget.Toast
import com.hyperdev.tungguin.model.katalogdesain.KatalogDesainResponse
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.network.ConnectivityStatus
import com.hyperdev.tungguin.network.HandleError
import com.hyperdev.tungguin.utils.SchedulerProvider
import com.hyperdev.tungguin.ui.view.KatalogDesainView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subscribers.ResourceSubscriber
import retrofit2.HttpException
import java.net.SocketTimeoutException

class CataloguePresenter(
    private val view: KatalogDesainView.View,
    private val context: Context,
    private val baseApiService: BaseApiService,
    private val scheduler: SchedulerProvider
) : KatalogDesainView.Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun getKatalogDesain(token: String) {
        view.displayProgress()

        compositeDisposable.add(
            baseApiService.getKatalogDesain(token, "application/json")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(scheduler.io())
                .subscribeWith(object : ResourceSubscriber<KatalogDesainResponse>() {
                    override fun onComplete() {
                        view.hideProgress()
                    }

                    override fun onNext(t: KatalogDesainResponse) {
                        try {
                            t.data?.let { view.showKatalogItemList(it) }
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