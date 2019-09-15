package com.hyperdev.tungguin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.model.detailproduct.PriceList
import com.hyperdev.tungguin.ui.view.PriceListView
import kotlinx.android.synthetic.main.desain_count_layout.view.*

class PriceListAdapter(
    private val priceList: ArrayList<PriceList>,
    private val priceListView: PriceListView
) :
    RecyclerView.Adapter<PriceListAdapter.ViewHolder>() {

    private var selected: ArrayList<Int> = arrayListOf()
    private var default = true

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.desain_count_layout, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int = priceList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (position == 0 && default) {
            val defPriceFormatted: String = priceList[0].priceFormatted.toString()
            val defDesainCount: String = priceList[0].designOptionCount.toString()
            val defDesainFormatted: String = priceList[0].formattedDesign.toString()
            val defPrice: String = priceList[0].price.toString()
            holder.itemView.number_desain.text = priceList[position].formattedDesign.toString()
            holder.itemView.price_desain.text = priceList[position].priceFormatted.toString()
            priceListView.shaowPriceList(
                defDesainFormatted,
                defPriceFormatted,
                defDesainCount,
                true,
                defPrice
            )

            if(default){
                holder.itemView.desain_layout.setBackgroundResource(R.drawable.btn_gray_round_no_hover)
            }else{
                holder.itemView.desain_layout.setBackgroundResource(R.drawable.btn_gray_round_hover)
            }
            selected.add(0)
        } else {
            if (!selected.contains(position)) {
                holder.itemView.desain_layout.setBackgroundResource(R.drawable.btn_gray_round_hover)
            } else {
                holder.itemView.desain_layout.setBackgroundResource(R.drawable.btn_gray_round_no_hover)
            }

            holder.itemView.number_desain.text = priceList[position].formattedDesign.toString()
            holder.itemView.price_desain.text = priceList[position].priceFormatted.toString()

            holder.itemView.desain_item.setOnClickListener {
                default = false

                val getPriceFormatted: String = priceList[position].priceFormatted.toString()
                val getDesainCount: String = priceList[position].designOptionCount.toString()
                val getDesainFormatted: String = priceList[position].formattedDesign.toString()
                val getPrice: String = priceList[position].price.toString()

                priceListView.shaowPriceList(
                    getDesainFormatted,
                    getPriceFormatted,
                    getDesainCount,
                    true,
                    getPrice
                )

                holder.itemView.desain_layout.setBackgroundResource(R.drawable.btn_gray_round_no_hover)

                if (selected.isEmpty()) {
                    selected.add(position)
                } else {
                    val oldPosition: Int = selected[0]
                    selected.clear()
                    selected.add(position)
                    notifyItemChanged(oldPosition)
                }
            }
        }
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}