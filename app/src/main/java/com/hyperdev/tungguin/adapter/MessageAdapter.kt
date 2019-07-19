package com.hyperdev.tungguin.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.hyperdev.tungguin.GlideApp
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.model.chat.ChatData
import com.hyperdev.tungguin.ui.activity.ChatActivity
import kotlinx.android.synthetic.main.message_customer.view.*
import kotlinx.android.synthetic.main.message_designer.view.*

class MessageAdapter(private val mContext: Context, private val options: MutableList<ChatData>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val viewHolder: RecyclerView.ViewHolder
        val view: View

        if (viewType == ChatActivity.TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.message_designer, parent, false)
            viewHolder = ReceivedMessageViewHolder(view)
        } else {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.message_customer, parent, false)
            viewHolder = SentMessageViewHolder(view)
        }

        return viewHolder
    }

    fun refreshAdapter(chatList: MutableList<ChatData>) {
        options.addAll(chatList)
        notifyItemRangeChanged(0, options.size)
    }

    fun setData(chatList: MutableList<ChatData>) {
        options.clear()
        options.addAll(chatList)
        notifyDataSetChanged()
    }

    fun addMessage(message: ChatData) {
        options.add(message)
        notifyItemInserted(options.size - 1)
    }

    override fun getItemViewType(position: Int): Int {
        return options[position].type ?: 0
    }

    override fun getItemCount(): Int = options.size

    override fun onBindViewHolder(@NonNull holder: RecyclerView.ViewHolder, position: Int) {

        val chatMessage = options[position]

        if (chatMessage.type == ChatActivity.TYPE_MESSAGE_RECEIVED) {

            val receiveMessage = holder.itemView.bubble_receive_message
            val receiveDate = holder.itemView.tv_receive_msg_date
            val receiveImage = holder.itemView.img_receive_message

            if (chatMessage.file.toString() != "null") {

                if (chatMessage.fileType.toString() == "image") {
                    receiveMessage.visibility = View.GONE
                    receiveImage.visibility = View.VISIBLE

                    GlideApp.with(mContext)
                        .load(chatMessage.file.toString())
                        .into(receiveImage)

                    receiveImage.setOnClickListener {
                        val intent = Intent()
                        intent.action = Intent.ACTION_VIEW
                        intent.data = Uri.parse(chatMessage.file.toString())
                        it.context.startActivity(intent)
                        (it.context as Activity)
                    }

                } else {
                    receiveMessage.setTextColor(Color.parseColor("#FF0012FF"))
                    receiveMessage.paintFlags = Paint.UNDERLINE_TEXT_FLAG
                    receiveMessage.text = chatMessage.file.toString()
                    receiveMessage.setOnClickListener {
                        val intent = Intent()
                        intent.action = Intent.ACTION_VIEW
                        intent.data = Uri.parse(chatMessage.file.toString())
                        it.context.startActivity(intent)
                        (it.context as Activity)
                    }
                }

                receiveDate.text = chatMessage.formattedDate.toString()
            } else {
                receiveMessage.visibility = View.VISIBLE
                receiveImage.visibility = View.GONE
                receiveMessage.text = chatMessage.text.toString()
                receiveDate.text = chatMessage.formattedDate.toString()
            }

        } else {

            val sendMessage = holder.itemView.bubble_send_message
            val sendDate = holder.itemView.tv_send_msg_date
            val sendImage = holder.itemView.img_send_message

            if (chatMessage.file.toString() != "null") {

                if (chatMessage.fileType.toString() == "image") {
                    sendMessage.visibility = View.GONE
                    sendImage.visibility = View.VISIBLE

                    GlideApp.with(mContext)
                        .load(chatMessage.file.toString())
                        .into(sendImage)

                    sendImage.setOnClickListener {
                        val intent = Intent()
                        intent.action = Intent.ACTION_VIEW
                        intent.data = Uri.parse(chatMessage.file.toString())
                        it.context.startActivity(intent)
                        (it.context as Activity)
                    }

                } else {
                    sendMessage.setTextColor(Color.parseColor("#FFFFFF"))
                    sendMessage.paintFlags = Paint.UNDERLINE_TEXT_FLAG
                    sendMessage.text = chatMessage.file.toString()
                    sendMessage.setOnClickListener {
                        val intent = Intent()
                        intent.action = Intent.ACTION_VIEW
                        intent.data = Uri.parse(chatMessage.file.toString())
                        it.context.startActivity(intent)
                        (it.context as Activity)
                    }
                }

                sendDate.text = chatMessage.formattedDate.toString()
            } else {
                sendMessage.visibility = View.VISIBLE
                sendImage.visibility = View.GONE
                sendMessage.text = chatMessage.text.toString()
                sendDate.text = chatMessage.formattedDate.toString()
            }

        }
    }

    internal class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    internal class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}