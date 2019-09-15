package com.hyperdev.tungguin.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.model.promodesain.PromoItem
import com.hyperdev.tungguin.model.promodesain.PromoData
import kotlinx.android.synthetic.main.katalog_list_layout.view.*
import kotlin.properties.Delegates

class PromoDesainAdapter(private val katalogList: ArrayList<PromoData>, private var context: Context?) :
    RecyclerView.Adapter<PromoDesainAdapter.ViewHolder>() {

    private var adapter by Delegates.notNull<PromoItemAdapter>()

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.katalog_list_layout, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int = katalogList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.itemView.tv_label_desain.text = katalogList[position].label.toString()

        adapter = PromoItemAdapter(katalogList[position].items as ArrayList<PromoItem>, context)

        val mContext = holder.itemView.context

        val layoutManager = LinearLayoutManager(mContext,
            LinearLayoutManager.HORIZONTAL, false)

        holder.itemView.rv_katalog_item.layoutManager = layoutManager
        holder.itemView.rv_katalog_item.setHasFixedSize(true)
        holder.itemView.rv_katalog_item.adapter = adapter
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}