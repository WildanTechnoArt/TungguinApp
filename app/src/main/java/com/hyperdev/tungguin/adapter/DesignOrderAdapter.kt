package com.hyperdev.tungguin.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.model.detailorder.ItemDesign
import android.text.util.Linkify
import kotlinx.android.synthetic.main.detail_product_item.view.*

class DesignOrderAdapter(private val designList: ArrayList<ItemDesign>) :
    RecyclerView.Adapter<DesignOrderAdapter.ViewHolder>() {

    private var expand: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.detail_product_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int = designList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        // Required
        val getNameDesign = designList[position].productName.toString()
        val getNumberDesign = designList[position].formattedNumber.toString()
        val getHargaDesign = designList[position].formattedPrice.toString()

        // Opsional
        val getKonsepDesign = designList[position].designConcept.toString()
        val getReferensiDesign = designList[position].preferenceArray.toString()
        val getDesignDetail = designList[position].designDetailObject.toString()
        val designDetail = getDesignDetail.replace("{", "").replace("\"", "")
            .replace("}", "").replace(":", ": ").replace(",", "\n")
            .replace("null", "-")

        holder.itemView.cl_table_product.visibility = View.GONE
        holder.itemView.tv_show_detail.text = getNameDesign
        holder.itemView.number_design.text = getNumberDesign

        if (getKonsepDesign != "null") {
            holder.itemView.design_concept.text = getKonsepDesign
        } else {
            holder.itemView.design_concept.text = "-"
        }

        if (getReferensiDesign != "[]") {
            holder.itemView.design_reference.text = getReferensiDesign
            Linkify.addLinks(holder.itemView.design_reference, Linkify.WEB_URLS)
        } else {
            holder.itemView.detail_design.text = "-"
        }

        holder.itemView.detail_design.text = designDetail

        holder.itemView.design_price.text = getHargaDesign

        holder.itemView.tv_show_detail.setOnClickListener {
            if (expand) {
                holder.itemView.cl_table_product.visibility = View.GONE
                expand = false
            } else {
                holder.itemView.cl_table_product.visibility = View.VISIBLE
                expand = true
            }
        }
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}