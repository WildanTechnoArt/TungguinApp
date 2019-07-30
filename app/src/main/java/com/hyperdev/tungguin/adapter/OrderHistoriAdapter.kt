package com.hyperdev.tungguin.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.model.historiorder.OrderItem
import com.hyperdev.tungguin.ui.activity.DetailOrderActivity
import com.hyperdev.tungguin.ui.activity.SearchDesignerActivity
import com.hyperdev.tungguin.utils.UtilsConstant.Companion.HASHED_ID
import kotlinx.android.synthetic.main.histori_order_layout.view.*

class OrderHistoriAdapter(private var context: Context?, private var orderList: ArrayList<OrderItem>) :
    RecyclerView.Adapter<OrderHistoriAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.histori_order_layout, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int = orderList.size

    fun refreshAdapter(orderList: List<OrderItem>) {
        this.orderList.addAll(orderList)
        notifyItemRangeChanged(0, this.orderList.size)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val getFormattedID = orderList[position].formattedId.toString()
        val getFormattedAmount = orderList[position].realTotalFormatted.toString()
        val getLabelOrder = orderList[position].statusFormatted?.label.toString()
        val getStatus = orderList[position].statusFormatted?.status.toString()
        val getHasherId = orderList[position].hashedId.toString()

        holder.itemView.listFormattedID.text = getFormattedID
        holder.itemView.listFormattedStatus.text = getLabelOrder
        holder.itemView.listFormattedAmount.text = getFormattedAmount
        when (getStatus) {
            "pending" -> {
                holder.itemView.listFormattedStatus.setBackgroundResource(R.drawable.orange_round_no_hover)
                holder.itemView.btn_pay.visibility = View.VISIBLE
                holder.itemView.btn_pay.setOnClickListener {
                    val intent = Intent(context, DetailOrderActivity::class.java)
                    intent.putExtra(HASHED_ID, getHasherId)
                    context?.startActivity(intent)
                    (context as Activity)
                }
            }
            "expired" -> {
                holder.itemView.listFormattedStatus.setBackgroundResource(R.drawable.btn_red_round_no_hover)
                holder.itemView.btn_detail.visibility = View.VISIBLE
                holder.itemView.btn_detail.setOnClickListener {
                    val intent = Intent(context, DetailOrderActivity::class.java)
                    intent.putExtra(HASHED_ID, getHasherId)
                    context?.startActivity(intent)
                    (context as Activity)
                }
            }
            "searching_designer" -> {
                holder.itemView.listFormattedStatus.setBackgroundResource(R.drawable.order_round_no_hover)
                holder.itemView.btn_detail.visibility = View.VISIBLE
                holder.itemView.btn_detail.setOnClickListener {
                    val intent = Intent(context, SearchDesignerActivity::class.java)
                    intent.putExtra(HASHED_ID, getHasherId)
                    context?.startActivity(intent)
                    (context as Activity)
                }
            }
            else -> {
                holder.itemView.listFormattedStatus.setBackgroundResource(R.drawable.order_round_no_hover)
                holder.itemView.btn_detail.visibility = View.VISIBLE
                holder.itemView.btn_detail.setOnClickListener {
                    val intent = Intent(context, DetailOrderActivity::class.java)
                    intent.putExtra(HASHED_ID, getHasherId)
                    context?.startActivity(intent)
                    (context as Activity)
                }
            }
        }
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}