package com.hyperdev.tungguin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.ui.TopupListener
import com.hyperdev.tungguin.model.topup.TopUpItemData
import kotlinx.android.synthetic.main.topup_item.view.*

class TopupListAdapter(private val topupList: ArrayList<TopUpItemData>, private val listener: TopupListener) :
    RecyclerView.Adapter<TopupListAdapter.ViewHolder>() {

    private var selected: ArrayList<Int> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.topup_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int = topupList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val label = topupList[position].label.toString()
        val amount = topupList[position].amount

        if (!selected.contains(position)) {
            holder.itemView.topup_layout.setBackgroundResource(R.drawable.btn_gray_round_hover)
        } else {
            holder.itemView.topup_layout.setBackgroundResource(R.drawable.btn_gray_round_no_hover)
        }

        holder.itemView.topup_label.text = label
        holder.itemView.setOnClickListener {
            listener.onClickListener(amount, label)

            holder.itemView.topup_layout.setBackgroundResource(R.drawable.btn_gray_round_no_hover)

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

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}