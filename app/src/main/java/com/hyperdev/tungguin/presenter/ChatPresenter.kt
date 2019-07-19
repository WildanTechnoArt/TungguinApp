package com.hyperdev.tungguin.presenter

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.google.gson.Gson
import com.hyperdev.tungguin.model.chat.ChatHistoriResponse
import com.hyperdev.tungguin.model.chat.MessageModel
import com.hyperdev.tungguin.model.detailorder.DetailOrderResponse
import com.hyperdev.tungguin.network.ConnectivityStatus
import com.hyperdev.tungguin.network.HandleError
import com.hyperdev.tungguin.network.Response
import com.hyperdev.tungguin.repository.chat.ChatRepositoryImp
import com.hyperdev.tungguin.utils.SchedulerProvider
import com.hyperdev.tungguin.ui.view.ChatView
import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subscribers.ResourceSubscriber
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.net.SocketTimeoutException

class ChatPresenter(
    private val view: ChatView.View,
    private val context: Context,
    private val chat: ChatRepositoryImp,
    private val scheduler: SchedulerProvider
) : ChatView.Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun sendMessage(
        token: String, accept: String,
        hashed_id: String, text: RequestBody, file: MultipartBody.Part?
    ) {

        view.showProgress()

        chat.chatRequest(token, accept, hashed_id, text, file)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(scheduler.io())
            .unsubscribeOn(scheduler.io())
            .subscribe(object : Observer<MessageModel> {
                override fun onComplete() {
                    view.onChatSuccess()
                }

                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onNext(t: MessageModel) {

                }

                override fun onError(e: Throwable) {
                    view.hideProgress()

                    if (ConnectivityStatus.isConnected(context)) {
                        when (e) {
                            is HttpException -> {
                                val gson = Gson()
                                val response =
                                    gson.fromJson(e.response().errorBody()?.charStream(), Response::class.java)
                                val message = response.meta?.message.toString()
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                            }
                            is SocketTimeoutException -> // connection errors
                                view.noInternetConnection("Connection Timeout!")
                        }
                    } else {
                        view.noInternetConnection("Tidak Terhubung Dengan Internet!")
                    }
                }

            })
    }

    override fun takeFile(activity: Activity) {
        view.showProgress()
        compositeDisposable.add(
            RxPaparazzo.single(activity)
                .usingFiles()
                .subscribeOn(scheduler.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it.resultCode() != Activity.RESULT_OK) {
                        view.hideProgress()
                        return@subscribe
                    }

                    view.onSuccess()
                    view.loadFile(it.data())
                }
        )
    }

    override fun getDetailOrder(token: String, id: String) {
        view.showProgress()
        compositeDisposable.add(
            chat.getOrderDetail(token, "application/json", id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(scheduler.io())
                .subscribeWith(object : ResourceSubscriber<DetailOrderResponse>() {
                    override fun onComplete() {
                        view.onSuccess()
                    }

                    override fun onNext(t: DetailOrderResponse) {
                        try {
                            t.data?.designer?.let { view.profileDesigner(it) }
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

    override fun getChatData(token: String, hasher_id: String, page: Int?) {
        view.showProgress()
        compositeDisposable.add(
            chat.getChatHistori(token, "application/json", hasher_id, page)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(scheduler.io())
                .subscribeWith(object : ResourceSubscriber<ChatHistoriResponse>() {
                    override fun onComplete() {
                        view.onSuccess()
                    }

                    override fun onNext(t: ChatHistoriResponse) {
                        try {
                            t.data?.dataChat?.let { view.showChatItem(it) }
                            view.showChatData(t.data)
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