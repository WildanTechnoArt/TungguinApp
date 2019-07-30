package com.hyperdev.tungguin.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.hyperdev.tungguin.GlideApp
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.model.katalogdesain.Item
import com.hyperdev.tungguin.ui.activity.DetailProductActivity
import com.hyperdev.tungguin.utils.UtilsConstant.Companion.HASHED_ID
import kotlinx.android.synthetic.main.catalog_item.view.*

class KatalogItemListAdapter(private val katalogItem: ArrayList<Item>, private var context: Context?) :
    RecyclerView.Adapter<KatalogItemListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.catalog_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int = katalogItem.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val getItemID = katalogItem[position].hashedId.toString()

        holder.itemView.tv_design_name.text = katalogItem[position].name.toString()

        GlideApp.with(holder.view.context)
            .load(katalogItem[position].iconUrl.toString())
            .transition(withCrossFade())
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.itemView.progress_bar.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.itemView.progress_bar.visibility = View.GONE
                    return false
                }

            })
            .into(holder.itemView.img_product_item)

        holder.itemView.tv_design_price.text = katalogItem[position].formattedPrice.toString()

        holder.itemView.catalog_item_view.setOnClickListener {
            val intent = Intent(context, DetailProductActivity::class.java)
            intent.putExtra(HASHED_ID, getItemID)
            context?.startActivity(intent)
            (context as Activity)
        }
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}