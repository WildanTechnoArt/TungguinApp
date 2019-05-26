package com.hyperdev.tungguin.adapter

import android.annotation.SuppressLint
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.model.detailorder.ItemDesign
import android.text.util.Linkify

class DesignOrderAdapter(private val designList: ArrayList<ItemDesign>)
    :RecyclerView.Adapter<DesignOrderAdapter.ViewHolder>(){

    private var kondisi = true

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        // Deklarasi View
        val nameDesign: TextView = view.findViewById(R.id.name_design)
        val konsepDesign: TextView = view.findViewById(R.id.konsep_design)
        val jumlahDesign: TextView = view.findViewById(R.id.number_design)
        val dokumenDesign: TextView = view.findViewById(R.id.dokumen)
        val hargaDesign: TextView = view.findViewById(R.id.price_product)
        val referensiDesign: TextView = view.findViewById(R.id.referensi_design)
        val detailDesign: TextView = view.findViewById(R.id.detail_design)
        val tableProduct: ConstraintLayout = view.findViewById(R.id.table_product)
        val showDetail: LinearLayout = view.findViewById(R.id.show_detail)
        val imageMoreItem: ImageView = view.findViewById(R.id.more_item)
    }

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
        val getDokumenDesign = designList[position].designDocumentUrl.toString()
        val getReferensiDesign = designList[position].preferenceArray.toString()
        val getDesignDetail = designList[position].designDetailObject.toString()
        val designDetail = getDesignDetail.replace("{", "").replace("\"", "")
            .replace("}", "").replace(":", ": ").replace(",", "\n")
            .replace("null", "-")

        holder.tableProduct.visibility = View.GONE
        holder.nameDesign.text = getNameDesign
        holder.jumlahDesign.text = getNumberDesign

        if(getKonsepDesign != "null"){
            holder.konsepDesign.text = getKonsepDesign
        }else{
            holder.konsepDesign.text = "-"
        }

        if(getDokumenDesign != "null"){
            holder.dokumenDesign.text = getDokumenDesign
            Linkify.addLinks(holder.dokumenDesign, Linkify.WEB_URLS)
        }else{
            holder.dokumenDesign.text = "-"
        }

        if(getReferensiDesign != "[]"){
            holder.referensiDesign.text = getReferensiDesign
            Linkify.addLinks(holder.referensiDesign, Linkify.WEB_URLS)
        }else{
            holder.referensiDesign.text = "-"
        }


        holder.detailDesign.text = designDetail

        holder.hargaDesign.text = getHargaDesign

        holder.showDetail.setOnClickListener {
            if(kondisi){
                holder.imageMoreItem.setImageResource(R.drawable.ic_expand_less_28dp)
                holder.tableProduct.visibility = View.VISIBLE
                kondisi = false
            }else{
                holder.imageMoreItem.setImageResource(R.drawable.ic_expand_more_28dp)
                holder.tableProduct.visibility = View.GONE
                kondisi = true
            }
        }

    }
}