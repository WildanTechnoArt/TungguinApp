package com.hyperdev.tungguin.adapter

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.model.detailproduct.PriceList
import com.hyperdev.tungguin.view.PriceListView

class PriceListAdapter(private val priceList: ArrayList<PriceList>, private val priceListView: PriceListView)
    :RecyclerView.Adapter<PriceListAdapter.ViewHolder>(){

    private var selected: ArrayList<Int> = arrayListOf()

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        // Deklarasi View
        val getNumberDesain: TextView = view.findViewById(R.id.number_desain)
        val getPriceDesain: TextView = view.findViewById(R.id.price_desain)
        val getDesainItem: CardView = view.findViewById(R.id.desain_item)
        val getDesainLayout: LinearLayout = view.findViewById(R.id.desain_layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.desain_count_layout, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int = priceList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if(!selected.contains(position)){
            holder.getDesainLayout.setBackgroundResource(R.drawable.menu_round_button)
        }
        else{
            holder.getDesainLayout.setBackgroundResource(R.drawable.menu_round_no_hover)
        }

        val getPriceFormatted: String = priceList[position].priceFormatted.toString()
        val getPriceNumber: String = priceList[position].designOptionCount.toString()
        val getPrice: String = priceList[position].price.toString()

        holder.getNumberDesain.text = priceList[position].designOptionCount.toString()
        holder.getPriceDesain.text = priceList[position].priceFormatted.toString()

        holder.getDesainItem.setOnClickListener {

            priceListView.shaowPriceList(getPriceFormatted, getPriceNumber, true, getPrice)

            holder.getDesainLayout.setBackgroundResource(R.drawable.menu_round_no_hover)

            if(selected.isEmpty()){
                selected.add(position)
            }else{
                val oldPosition: Int = selected[0]
                selected.clear()
                selected.add(position)
                notifyItemChanged(oldPosition)
            }
        }
    }
}