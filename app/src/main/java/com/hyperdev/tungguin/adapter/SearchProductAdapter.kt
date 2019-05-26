package com.hyperdev.tungguin.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.request.RequestOptions
import com.hyperdev.tungguin.GlideApp
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.model.searchproduct.DesignItem
import com.hyperdev.tungguin.view.ui.DetailProductActivity

class SearchProductAdapter(private val productList: ArrayList<DesignItem>, private var context: Context?)
    :RecyclerView.Adapter<SearchProductAdapter.ViewHolder>(){

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        // Deklarasi View
        val getDesignName: TextView = view.findViewById(R.id.design_name)
        val getBalanceProduct: TextView = view.findViewById(R.id.balance_design)
        val getImageProduct: ImageView = view.findViewById(R.id.image_product)
        val getItemSelector: ConstraintLayout = view.findViewById(R.id.itemTopup)
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.search_design_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int = productList.size

    fun refreshAdapter(productList: List<DesignItem>) {
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
            .apply(RequestOptions().placeholder(R.drawable.ic_hourglass_empty_black_24dp))
            .into(holder.getImageProduct)

        holder.getItemSelector.setOnClickListener {
            val intent = Intent(context, DetailProductActivity::class.java)
            intent.putExtra("sendProductID", getItemID)
            context?.startActivity(intent)
            (context as Activity)
        }
    }
}