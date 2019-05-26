package com.hyperdev.tungguin.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.model.katalogdesain.KatalogItem
import kotlin.properties.Delegates

class KatalogDesainAdapter(private val katalogList: ArrayList<KatalogItem>, private var context: Context?)
    :RecyclerView.Adapter<KatalogDesainAdapter.ViewHolder>(){

    private var listItem: MutableList<KatalogItem> = mutableListOf()
    private var adapter by Delegates.notNull<KatalogItemListAdapter>()

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        // Deklarasi View
        val getKatalogName: TextView = view.findViewById(R.id.label_desain)
        val getItemKatalog: RecyclerView = view.findViewById(R.id.desain_item_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.katalog_list_layout, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int = katalogList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        listItem.addAll(katalogList)

        holder.getKatalogName.text = katalogList[position].label.toString()

        adapter = KatalogItemListAdapter(listItem as ArrayList<KatalogItem>, context, position)

        val layout = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        holder.getItemKatalog.layoutManager = layout
        holder.getItemKatalog.setHasFixedSize(true)
        holder.getItemKatalog.adapter = adapter
    }
}