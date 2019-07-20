package com.hyperdev.tungguin.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hyperdev.tungguin.model.chat.ChatListItem
import com.hyperdev.tungguin.model.chat.ChatListResponse
import com.hyperdev.tungguin.repository.chat.ChatRepositoryImp
import com.hyperdev.tungguin.ui.view.ChatListView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.ResourceSubscriber

class ChatListViewModel : ViewModel(), ChatListView.ViewModel {

    private val compositeDisposable = CompositeDisposable()
    private val chatList = MutableLiveData<ArrayList<ChatListItem>>()

    fun getChatList(): LiveData<ArrayList<ChatListItem>>{
        return chatList
    }

    override fun setChatList(
        repository: ChatRepositoryImp,
        view: ChatListView.View,
        token: String,
        accept: String,
        page: Int?
    ) {

        view.showProgressBar()
        compositeDisposable.add(
            repository.getChatList(token, accept, page)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(object : ResourceSubscriber<ChatListResponse>(){
                    override fun onComplete() {
                        view.onSuccess()
                    }

                    override fun onNext(t: ChatListResponse?) {
                        chatList.postValue(t?.data?.chatListItem)
                        t?.data?.let { view.showChatListData(it) }
                    }

                    override fun onError(t: Throwable?) {
                        t?.printStackTrace()
                        view.hideProgressBar()
                        view.handleError(t)
                    }
                })
        )

    }

    override fun onDestroy() {
        compositeDisposable.dispose()
    }
}