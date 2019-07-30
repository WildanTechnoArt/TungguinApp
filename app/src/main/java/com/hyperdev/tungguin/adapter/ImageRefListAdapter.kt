package com.hyperdev.tungguin.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.ui.view.DeleteImageView
import kotlinx.android.synthetic.main.image_list_layout.view.*

class ImageRefListAdapter(private var nameImgList: ArrayList<String?>, private var imageView: DeleteImageView) :
    RecyclerView.Adapter<ImageRefListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.image_list_layout, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return nameImgList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val getImgName = nameImgList[position].toString()

        holder.itemView.image_name.text = getImgName

        holder.itemView.file_close.setOnClickListener {
            onDeleteData(position)
            imageView.onDeleteImage(position)
        }
    }

    private fun onDeleteData(position: Int?) {
        nameImgList.removeAt(position!!)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, nameImgList.size)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}