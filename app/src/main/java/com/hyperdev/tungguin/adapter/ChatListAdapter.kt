package com.hyperdev.tungguin.adapter

import android.annotation.SuppressLint
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hyperdev.tungguin.GlideApp
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.model.chat.ChatListItem
import com.hyperdev.tungguin.ui.activity.ChatActivity
import com.hyperdev.tungguin.utils.UtilsConstant.Companion.HASHED_ID
import kotlinx.android.synthetic.main.chat_list_item.view.*

class ChatListAdapter : RecyclerView.Adapter<ChatListAdapter.ViewHolder>() {

    private var chats = ArrayList<ChatListItem>()

    fun setData(chats: ArrayList<ChatListItem>) {
        this.chats.clear()
        this.chats.addAll(chats)
        notifyDataSetChanged()
    }

    fun refreshAdapter(chats: List<ChatListItem>) {
        this.chats.addAll(chats)
        notifyItemRangeChanged(0, this.chats.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.chat_list_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int = chats.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val chat = chats[position]

        val clItemView = holder.itemView.cl_item_view
        val tvDesignName = holder.itemView.tv_design_name
        val tvDesignerId = holder.itemView.tv_designer_id
        val tvOrderId = holder.itemView.tv_order_id
        val imgPhotoDesigner = holder.itemView.img_photo_designer
        val mContext = holder.itemView.context

        val getHashedId = chat.hashedId.toString()

        tvDesignName.text = chat.designer?.name.toString()
        tvDesignerId.text = "Kode: ${chat.designer?.formattedId.toString()}"
        tvOrderId.text = "ID Order: ${chat.formattedId.toString()}"

        GlideApp.with(mContext)
            .load(chat.designer?.photoUrl.toString())
            .placeholder(R.drawable.profile_placeholder)
            .into(imgPhotoDesigner)

        clItemView.setOnClickListener {
            val intent = Intent(it.context, ChatActivity::class.java)
            intent.putExtra(HASHED_ID, getHashedId)
            it.context.startActivity(intent)
        }

    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}