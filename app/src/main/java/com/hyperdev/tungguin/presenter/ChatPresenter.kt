package com.hyperdev.tungguin.presenter

import android.app.Activity
import com.hyperdev.tungguin.model.chat.ChatHistoryResponse
import com.hyperdev.tungguin.model.chat.MessageModel
import com.hyperdev.tungguin.model.detailorder.DetailOrderResponse
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.ui.view.ChatView
import com.hyperdev.tungguin.utils.SchedulerProvider
import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subscribers.ResourceSubscriber
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ChatPresenter(
    private val view: ChatView.View,
    private val baseApiService: BaseApiService,
    private val scheduler: SchedulerProvider
) : ChatView.Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun sendMessage(
        token: String, accept: String,
        hashed_id: String, text: RequestBody, file: MultipartBody.Part?
    ) {

        view.showProgress()

        baseApiService.chatRequest(token, accept, hashed_id, text, file)
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
                    view.handleError(e)
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
            baseApiService.getOrderDetail(token, "application/json", id)
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
                        view.hideProgress()
                        view.handleError(e)
                    }
                })
        )
    }

    override fun getChatData(token: String, hasher_id: String, page: Int?) {
        view.showProgress()
        compositeDisposable.add(
            baseApiService.getChatHistori(token, "application/json", hasher_id, page)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(scheduler.io())
                .subscribeWith(object : ResourceSubscriber<ChatHistoryResponse>() {
                    override fun onComplete() {
                        view.onSuccess()
                    }

                    override fun onNext(t: ChatHistoryResponse) {
                        try {
                            t.data?.dataChat?.let { view.showChatItem(it) }
                            view.showChatData(t.data)
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