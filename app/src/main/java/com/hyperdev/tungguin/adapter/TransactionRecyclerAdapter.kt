package com.hyperdev.tungguin.adapter

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.model.transactionhistory.ListTransaction

class TransactionRecyclerAdapter(private val transactionList: ArrayList<ListTransaction>)
    :RecyclerView.Adapter<TransactionRecyclerAdapter.ViewHolder>(){

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        //Deklarasi View
        val getListFormattedID: TextView = view.findViewById(R.id.listFormattedID)
        val getListFormattedAmount: TextView = view.findViewById(R.id.listFormattedAmount)
        val getListFormattedDate: TextView = view.findViewById(R.id.listFormattedDate)
    }

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
        holder.getListFormattedID.text = getFormattedID
        if(getListFormattedType == "deposit"){
            holder.getListFormattedAmount.text = "+ $getFormattedAmount"
        }else if(getListFormattedType == "withdraw"){
            holder.getListFormattedAmount.text = "- $getFormattedAmount"
        }
        holder.getListFormattedDate.text = getFormattedDate
    }
}