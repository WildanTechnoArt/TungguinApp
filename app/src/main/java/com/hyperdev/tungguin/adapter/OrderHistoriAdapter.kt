package com.hyperdev.tungguin.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.model.historiorder.OrderItem
import com.hyperdev.tungguin.ui.activity.DetailOrderActivity
import com.hyperdev.tungguin.ui.activity.SearchDesignerActivity

class OrderHistoriAdapter(private var context: Context?, private var orderList: ArrayList<OrderItem>) :
    RecyclerView.Adapter<OrderHistoriAdapter.ViewHolder>() {

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        //Deklarasi View
        val getListFormattedID: TextView = itemView.findViewById(R.id.listFormattedID)
        val getListFormattedStatus: TextView = itemView.findViewById(R.id.listFormattedStatus)
        val getListFormattedAmount: TextView = itemView.findViewById(R.id.listFormattedAmount)
        val btnDetailOrder: Button = itemView.findViewById(R.id.btn_detail)
        val btnPayment: Button = itemView.findViewById(R.id.btn_pay)
    }

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

        holder.getListFormattedID.text = getFormattedID
        holder.getListFormattedStatus.text = getLabelOrder
        holder.getListFormattedAmount.text = getFormattedAmount
        when (getStatus) {
            "pending" -> {
                holder.getListFormattedStatus.setBackgroundResource(R.drawable.orange_round_no_hover)
                holder.btnPayment.visibility = View.VISIBLE
                holder.btnPayment.setOnClickListener {
                    val intent = Intent(context, DetailOrderActivity::class.java)
                    intent.putExtra("sendOrderID", getHasherId)
                    context?.startActivity(intent)
                    (context as Activity)
                }
            }
            "expired" -> {
                holder.getListFormattedStatus.setBackgroundResource(R.drawable.red_round_no_hover)
                holder.btnDetailOrder.visibility = View.VISIBLE
                holder.btnDetailOrder.setOnClickListener {
                    val intent = Intent(context, DetailOrderActivity::class.java)
                    intent.putExtra("sendOrderID", getHasherId)
                    context?.startActivity(intent)
                    (context as Activity)
                }
            }
            "searching_designer" -> {
                holder.getListFormattedStatus.setBackgroundResource(R.drawable.order_round_no_hover)
                holder.btnDetailOrder.visibility = View.VISIBLE
                holder.btnDetailOrder.setOnClickListener {
                    val intent = Intent(context, SearchDesignerActivity::class.java)
                    intent.putExtra("sendOrderID", getHasherId)
                    context?.startActivity(intent)
                    (context as Activity)
                }
            }
            else -> {
                holder.getListFormattedStatus.setBackgroundResource(R.drawable.order_round_no_hover)
                holder.btnDetailOrder.visibility = View.VISIBLE
                holder.btnDetailOrder.setOnClickListener {
                    val intent = Intent(context, DetailOrderActivity::class.java)
                    intent.putExtra("sendOrderID", getHasherId)
                    context?.startActivity(intent)
                    (context as Activity)
                }
            }
        }
    }
}