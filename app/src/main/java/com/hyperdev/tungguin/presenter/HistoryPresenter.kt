package com.hyperdev.tungguin.presenter

import android.content.Context
import android.widget.Toast
import com.hyperdev.tungguin.model.transaction.TransactionResponse
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.network.ConnectivityStatus
import com.hyperdev.tungguin.network.HandleError
import com.hyperdev.tungguin.utils.SchedulerProvider
import com.hyperdev.tungguin.ui.view.BalanceView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subscribers.ResourceSubscriber
import retrofit2.HttpException
import java.net.SocketTimeoutException

class HistoryPresenter(
    private val view: BalanceView.View,
    private val context: Context,
    private val baseApiService: BaseApiService,
    private val scheduler: SchedulerProvider
) : BalanceView.Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun getUserBalance(token: String) {
        view.showProgressBar()

        compositeDisposable.add(
            baseApiService.getTransactionBalance(token, "application/json")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(scheduler.io())
                .subscribeWith(object : ResourceSubscriber<TransactionResponse>() {
                    override fun onComplete() {
                        view.onSuccess()
                    }

                    override fun onNext(t: TransactionResponse) {
                        try {
                            t.data?.let { view.showTransaction(it) }
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        view.hideProgressBar()

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