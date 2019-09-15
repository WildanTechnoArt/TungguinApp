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
import com.hyperdev.tungguin.model.promodesain.BannerData
import com.hyperdev.tungguin.ui.activity.BannerContentActivity
import com.hyperdev.tungguin.utils.UtilsConstant.Companion.BANNER_NAME
import com.hyperdev.tungguin.utils.UtilsConstant.Companion.BANNER_URL
import kotlinx.android.synthetic.main.banner_item.view.*

class BannerItemAdapter(private val bannerItem: ArrayList<BannerData>, private var context: Context?) :
    RecyclerView.Adapter<BannerItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.banner_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int = bannerItem.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.setIsRecyclable(false)

        val bannerUrl = bannerItem[position].url
        val bannerName = bannerItem[position].title.toString()

        GlideApp.with(holder.view.context)
            .load(bannerItem[position].bannerUrl.toString())
            .transition(withCrossFade())
            .placeholder(R.drawable.ic_image_ref)
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
            .into(holder.itemView.img_banner)

        holder.itemView.banner_item.setOnClickListener {
            if(bannerUrl != null){
                val intent = Intent(context, BannerContentActivity::class.java)
                intent.putExtra(BANNER_URL, bannerUrl.toString())
                intent.putExtra(BANNER_NAME, bannerName)
                context?.startActivity(intent)
                (context as Activity)
            }
        }
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}