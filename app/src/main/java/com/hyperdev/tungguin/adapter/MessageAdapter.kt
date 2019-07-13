package com.hyperdev.tungguin.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.github.library.bubbleview.BubbleTextView
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.model.chat.ChatData
import com.hyperdev.tungguin.ui.activity.ChatActivity

class MessageAdapter(private val mContext: Context, private val options: ArrayList<ChatData>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val viewHolder: RecyclerView.ViewHolder
        val view: View

        if (viewType == ChatActivity.TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(mContext)
                .inflate(R.layout.layout_msg_friend, parent, false)
            viewHolder = ReceivedMessageViewHolder(view)
        } else {
            view = LayoutInflater.from(mContext)
                .inflate(R.layout.layout_msg_user, parent, false)
            viewHolder = SentMessageViewHolder(view)
        }

        return viewHolder
    }

    fun refreshAdapter(chatList: List<ChatData>) {
        this.options.addAll(chatList)
        notifyItemRangeChanged(0, this.options.size)
    }

    fun addMessage(message: ChatData) {
        this.options.add(message)
        notifyItemInserted(options.size - 1)
    }

    override fun getItemViewType(position: Int): Int {
        return options[position].type!!
    }

    override fun getItemCount(): Int = options.size

    override fun onBindViewHolder(@NonNull holder: RecyclerView.ViewHolder, position: Int) {

        val chatMessage = options[position]

        if (chatMessage.type == ChatActivity.TYPE_MESSAGE_RECEIVED) {

            @Suppress("SENSELESS_COMPARISON")
            if (chatMessage.file.toString() != "null") {
                (holder as ReceivedMessageViewHolder).chatView.setTextColor(Color.parseColor("#FF0012FF"))
                holder.chatView.paintFlags = Paint.UNDERLINE_TEXT_FLAG
                holder.chatView.text = chatMessage.file.toString()
                holder.chatView.setOnClickListener {
                    val intent = Intent()
                    intent.action = Intent.ACTION_VIEW
                    intent.data = Uri.parse(chatMessage.file.toString())
                    it.context.startActivity(intent)
                    (it.context as Activity)
                }
                holder.dateView.text = chatMessage.formattedDate.toString()
            } else {
                (holder as ReceivedMessageViewHolder).chatView.text = chatMessage.text.toString()
                holder.dateView.text = chatMessage.formattedDate.toString()
            }

        } else {

            @Suppress("SENSELESS_COMPARISON")
            if (chatMessage.file.toString() != "null") {
                (holder as SentMessageViewHolder).chatView.setTextColor(Color.parseColor("#FFFFFF"))
                holder.chatView.paintFlags = Paint.UNDERLINE_TEXT_FLAG
                holder.chatView.text = chatMessage.file.toString()
                holder.chatView.setOnClickListener {
                    val intent = Intent()
                    intent.action = Intent.ACTION_VIEW
                    intent.data = Uri.parse(chatMessage.file.toString())
                    it.context.startActivity(intent)
                    (it.context as Activity)
                }
                holder.dateView.text = chatMessage.formattedDate.toString()
            } else {
                (holder as SentMessageViewHolder).chatView.text = chatMessage.text.toString()
                holder.dateView.text = chatMessage.formattedDate.toString()
            }

        }
    }

    internal class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var chatView: BubbleTextView = itemView.findViewById(R.id.msg_text)
        var dateView: TextView = itemView.findViewById(R.id.msg_date)

    }

    internal class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var chatView: BubbleTextView = itemView.findViewById(R.id.msg_text)
        var dateView: TextView = itemView.findViewById(R.id.msg_date)

    }
}