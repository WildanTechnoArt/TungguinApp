package com.hyperdev.tungguin.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.request.RequestOptions
import com.hyperdev.tungguin.GlideApp
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.model.katalogdesain.KatalogItem
import com.hyperdev.tungguin.view.ui.DetailProductActivity

class KatalogItemListAdapter(private val katalogItem: ArrayList<KatalogItem>, private var context: Context?,
                             private var itemPosition: Int)
    :RecyclerView.Adapter<KatalogItemListAdapter.ViewHolder>(){

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        // Deklarasi View
        val getProductName: TextView = view.findViewById(R.id.nama_desain)
        val getProductImage: ImageView = view.findViewById(R.id.itemImage)
        val getProductPrice: TextView = view.findViewById(R.id.desain_price)
        val getItemView: CardView = view.findViewById(R.id.itemClickDesain)
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_desain_layout, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int = katalogItem.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val getItemID = katalogItem[itemPosition].items?.get(position)?.hashedId.toString()

        holder.getProductName.text = katalogItem[itemPosition].items?.get(position)?.name.toString()

        GlideApp.with(holder.view.context)
            .load(katalogItem[itemPosition].items?.get(position)?.iconUrl.toString())
            .apply(RequestOptions().placeholder(R.drawable.ic_hourglass_empty_black_24dp))
            .into(holder.getProductImage)

        holder.getProductPrice.text = katalogItem[itemPosition].items?.get(position)?.formattedPrice.toString()

        holder.getItemView.setOnClickListener {
            val intent = Intent(context, DetailProductActivity::class.java)
            intent.putExtra("sendProductID", getItemID)
            context?.startActivity(intent)
            (context as Activity)
        }
    }
}