package com.hyperdev.tungguin.view

import android.app.Activity
import com.hyperdev.tungguin.model.chat.ChatData
import com.hyperdev.tungguin.model.chat.HistoriItem
import com.hyperdev.tungguin.model.detailorder.DesainerProfile
import com.miguelbcr.ui.rx_paparazzo2.entities.FileData

class ChatHistoriView {

    interface View{
        fun showChatItem(chatlist: List<ChatData>)
        fun showChatData(historiItem: HistoriItem?)
        fun profileDesigner(designer: DesainerProfile)
        fun loadFile(file: FileData?)
        fun showProgress()
        fun hideProgress()
        fun onSuccess()
    }

    interface Presenter{
        fun getChatData(token: String, hasher_id: String, page: Int)
        fun getDetailOrder(token: String, id: String)
        fun takeFile(activity: Activity)
        fun onDestroy()
    }
}