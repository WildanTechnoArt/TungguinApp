package com.hyperdev.tungguin.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.hyperdev.tungguin.BuildConfig
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.model.topuphistori.ListTopUp
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme
import com.midtrans.sdk.corekit.models.snap.TransactionResult
import com.midtrans.sdk.uikit.SdkUIFlowBuilder

class TopUpRecyclerAdapter(private var context: Context?, private var topupList: ArrayList<ListTopUp>)
    :RecyclerView.Adapter<TopUpRecyclerAdapter.ViewHolder>(){

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        //Deklarasi View
        val getListFormattedID: TextView = itemView.findViewById(R.id.listFormattedID)
        val getListFormattedStatus: TextView = itemView.findViewById(R.id.listFormattedStatus)
        val getListFormattedAmount: TextView = itemView.findViewById(R.id.listFormattedAmount)
        val getListFormattedDate: TextView = itemView.findViewById(R.id.listType)
        val getListFormattedExpireAt: TextView = itemView.findViewById(R.id.listFormattedDate)
        val getListItemView: ConstraintLayout = itemView.findViewById(R.id.itemTopup)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.histori_topup_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int = topupList.size

    fun refreshAdapter(topupList: List<ListTopUp>) {
        this.topupList.addAll(topupList)
        notifyItemRangeChanged(0, this.topupList.size)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val getFormattedID = topupList[position].formattedId.toString()
        val getFormattedStatus = topupList[position].formattedStatus.toString()
        val getFormattedAmount = topupList[position].formattedAmount.toString()
        val getFormattedDate = topupList[position].formattedDate.toString()
        val getColorHex = topupList[position].statusColorHex.toString()
        val getFormattedExpireAt = topupList[position].formattedExpireAt.toString()
        val getMidtransToken = topupList[position].midtransToken

        //set data dari ListTopUp pada View
        holder.getListFormattedID.text = getFormattedID
        holder.getListFormattedStatus.setBackgroundColor(Color.parseColor(getColorHex))
        holder.getListFormattedStatus.text = getFormattedStatus
        holder.getListFormattedAmount.text = getFormattedAmount
        holder.getListFormattedDate.text = "Date: $getFormattedDate"
        holder.getListFormattedExpireAt.text = getFormattedExpireAt
        holder.getListItemView.setOnClickListener {
            @Suppress("SENSELESS_COMPARISON")
            if(getMidtransToken != null){
                midtransInitialotation()
                MidtransSDK.getInstance().startPaymentUiFlow(context, getMidtransToken.toString())
            }
        }
    }

    private fun midtransInitialotation(){
        SdkUIFlowBuilder.init()
            .setClientKey(BuildConfig.MIDTRANS_CLIENTID)
            .setContext(context)
            .setTransactionFinishedCallback {
                if (it.response != null) {
                    when (it.status) {
                        TransactionResult.STATUS_SUCCESS -> {
                            Toast.makeText(
                                context,
                                "Transaction Finished ",
                                Toast.LENGTH_LONG
                            ).show()
                            (context as Activity).finish()
                        }
                        TransactionResult.STATUS_PENDING -> (context as Activity).finish()
                        TransactionResult.STATUS_FAILED -> Toast.makeText(
                            context,
                            "Transaction Failed",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    it.response.validationMessages
                } else if (it.isTransactionCanceled) {
                    Toast.makeText(context, "Transaction Canceled", Toast.LENGTH_LONG).show()
                } else {
                    if (it.status.equals(TransactionResult.STATUS_INVALID, ignoreCase = true)) {
                        Toast.makeText(context, "Transaction Invalid", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "Transaction Finished with failure.", Toast.LENGTH_LONG).show()
                    }
                }
            }
            .setMerchantBaseUrl("https://app.tungguin.com/") //set merchant url (required)
            .enableLog(true) // enable sdk log (optional)
            .setColorTheme(
                CustomColorTheme(
                    "#FFE51255",
                    "#B61548",
                    "#FFE51255"
                )
            ) // set theme. it will replace theme on snap theme on MAP ( optional)
            .buildSDK()
    }
}