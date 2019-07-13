package com.hyperdev.tungguin.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.TextView
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.ui.view.DeleteImageView

class ImageRefListAdapter(private var nameImgList: ArrayList<String?>, private var imageView: DeleteImageView) :
    RecyclerView.Adapter<ImageRefListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        //Deklarasi Variable
        var nameImage: TextView = itemView.findViewById(R.id.image_name)
        var closeImage: ImageButton = itemView.findViewById(R.id.file_close)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.image_list_layout, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return nameImgList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        //Inisialisasi DashboardData pada View
        val getImgName = nameImgList[position].toString()

        //Deklarasi DashboardData pada View
        holder.nameImage.text = getImgName

        holder.closeImage.setOnClickListener {
            onDeleteData(position)
            imageView.onDeleteImage(position)
        }
    }

    private fun onDeleteData(position: Int?) {
        nameImgList.removeAt(position!!)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, nameImgList.size)
    }
}