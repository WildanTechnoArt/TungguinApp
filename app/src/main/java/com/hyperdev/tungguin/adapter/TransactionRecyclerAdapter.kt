package com.hyperdev.tungguin.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.model.transaction.ListTransaction
import kotlinx.android.synthetic.main.histori_transaction_item.view.*

class TransactionRecyclerAdapter(private val transactionList: ArrayList<ListTransaction>) :
    RecyclerView.Adapter<TransactionRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.histori_transaction_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int = transactionList.size

    fun refreshAdapter(transactionList: List<ListTransaction>) {
        this.transactionList.addAll(transactionList)
        notifyItemRangeChanged(0, this.transactionList.size)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        //Mendapatkan data-data dari ListTopUp
        val getFormattedID = transactionList[position].meta?.description.toString()
        val getFormattedAmount = transactionList[position].formattedAmount.toString()
        val getFormattedDate = transactionList[position].formattedDate.toString()
        val getListFormattedType = transactionList[position].type.toString()

        //set data dari ListTopUp pada View
        holder.itemView.listFormattedID.text = getFormattedID

        if (getListFormattedType == "deposit") {
            holder.itemView.listFormattedAmount.text = "+ $getFormattedAmount"
        } else if (getListFormattedType == "withdraw") {
            holder.itemView.listFormattedAmount.text = "- $getFormattedAmount"
        }

        holder.itemView.listFormattedDate.text = getFormattedDate
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}