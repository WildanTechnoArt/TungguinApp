package com.hyperdev.tungguin.ui.view

import com.hyperdev.tungguin.model.chat.ChatListData
import com.hyperdev.tungguin.network.BaseApiService

class ChatListView {

    interface View {
        fun showChatListData(data: ChatListData)
        fun showProgressBar()
        fun hideProgressBar()
        fun onSuccess()
        fun handleError(t: Throwable?)
    }

    interface ViewModel {
        fun setChatList(baseApiService: BaseApiService, view: View, token: String, accept: String, page: Int?)
        fun onDestroy()
    }
}