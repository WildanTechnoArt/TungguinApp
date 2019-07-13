package com.hyperdev.tungguin.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.hyperdev.tungguin.GlideApp
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.model.searchproduct.SearchItem
import com.hyperdev.tungguin.ui.activity.DetailProductActivity

class SearchProductAdapter(private var context: Context?, private val productList: ArrayList<SearchItem>) :
    RecyclerView.Adapter<SearchProductAdapter.ViewHolder>() {

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        // Deklarasi View
        val getDesignName: TextView = view.findViewById(R.id.design_name)
        val getBalanceProduct: TextView = view.findViewById(R.id.balance_design)
        val getImageProduct: ImageView = view.findViewById(R.id.image_product)
        val getItemSelector: ConstraintLayout = view.findViewById(R.id.item_layout)
        val progressbar: ProgressBar = view.findViewById(R.id.progress_bar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_product_list, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int = productList.size

    fun refreshAdapter(productList: List<SearchItem>) {
        this.productList.addAll(productList)
        notifyItemRangeChanged(0, this.productList.size)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val getNameProduct = productList[position].name.toString()
        val getAmountProduct = productList[position].formattedPrice.toString()
        val getImageProduct = productList[position].iconUrl.toString()
        val getItemID = productList[position].hashedId.toString()

        holder.getDesignName.text = getNameProduct
        holder.getBalanceProduct.text = getAmountProduct
        GlideApp.with(holder.view.context)
            .load(getImageProduct)
            .override(110,90)
            .transition(withCrossFade())
            .listener(object : RequestListener<Drawable>{
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.progressbar.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.progressbar.visibility = View.GONE
                    return false
                }

            })
            .into(holder.getImageProduct)

        holder.getItemSelector.setOnClickListener {
            val intent = Intent(context, DetailProductActivity::class.java)
            intent.putExtra("sendProductID", getItemID)
            context?.startActivity(intent)
            (context as Activity)
        }
    }
}