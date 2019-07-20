package com.hyperdev.tungguin.ui.view

import com.hyperdev.tungguin.model.chat.ChatListData
import com.hyperdev.tungguin.repository.chat.ChatRepositoryImp

class ChatListView {

    interface View {
        fun showChatListData(data: ChatListData)
        fun showProgressBar()
        fun hideProgressBar()
        fun onSuccess()
        fun handleError(t: Throwable?)
    }

    interface ViewModel {
        fun setChatList(repository: ChatRepositoryImp, view: View, token: String, accept: String, page: Int?)
        fun onDestroy()
    }
}