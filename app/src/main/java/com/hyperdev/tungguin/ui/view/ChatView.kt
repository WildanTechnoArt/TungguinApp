package com.hyperdev.tungguin.ui.view

import android.app.Activity
import com.hyperdev.tungguin.model.chat.ChatData
import com.hyperdev.tungguin.model.chat.HistoriItem
import com.hyperdev.tungguin.model.detailorder.DesainerProfile
import com.miguelbcr.ui.rx_paparazzo2.entities.FileData
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ChatView {

    interface View {
        fun showChatItem(chatlist: MutableList<ChatData>)
        fun showChatData(historiItem: HistoriItem?)
        fun profileDesigner(designer: DesainerProfile)
        fun loadFile(file: FileData?)
        fun showProgress()
        fun hideProgress()
        fun onSuccess()
        fun onChatSuccess()
        fun noInternetConnection(message: String)
    }

    interface Presenter {
        fun getChatData(token: String, hasher_id: String, page: Int?)
        fun sendMessage(
            token: String, accept: String, hashed_id: String,
            text: RequestBody, file: MultipartBody.Part?
        )

        fun getDetailOrder(token: String, id: String)
        fun takeFile(activity: Activity)
        fun onDestroy()
    }
}