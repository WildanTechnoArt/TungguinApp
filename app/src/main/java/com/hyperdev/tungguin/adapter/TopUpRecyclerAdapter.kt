package com.hyperdev.tungguin.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.hyperdev.tungguin.BuildConfig
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.model.topup.ListTopUp
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.core.UIKitCustomSetting
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme
import com.midtrans.sdk.corekit.models.snap.TransactionResult
import com.midtrans.sdk.uikit.SdkUIFlowBuilder
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.android.synthetic.main.histori_topup_item.view.*

class TopUpRecyclerAdapter(private var context: Context?, private var topupList: ArrayList<ListTopUp>) :
    RecyclerView.Adapter<TopUpRecyclerAdapter.ViewHolder>() {

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
        val getPdfUrl = topupList[position].pdfUrl

        //set data dari ListTopUp pada View
        holder.itemView.listFormattedID.text = getFormattedID
        holder.itemView.listFormattedStatus.setBackgroundColor(Color.parseColor(getColorHex))
        holder.itemView.listFormattedStatus.text = getFormattedStatus
        holder.itemView.listFormattedAmount.text = getFormattedAmount
        holder.itemView.listType.text = "Date: $getFormattedDate"
        holder.itemView.listFormattedDate.text = getFormattedExpireAt
        holder.itemView.itemTopup.setOnClickListener {

            @Suppress("SENSELESS_COMPARISON")
            if (getMidtransToken != null) {
                if (getPdfUrl != null){
                    try {
                        val pdfUrl = Uri.parse(getPdfUrl)
                        val intent = Intent(Intent.ACTION_VIEW, pdfUrl)
                        context?.startActivity(intent)
                    } catch (ex: ActivityNotFoundException) {
                        FancyToast.makeText(
                            context,
                            "Tidak ada aplikasi yang dapat menangani permintaan ini. Silakan instal browser web",
                            FancyToast.LENGTH_LONG,
                            FancyToast.INFO,
                            false
                        ).show()
                        ex.printStackTrace()
                    }
                }else{
                    midtransInitialotation()
                    val uiKitCustomSetting: UIKitCustomSetting = MidtransSDK.getInstance().uiKitCustomSetting
                    uiKitCustomSetting.isSkipCustomerDetailsPages = true
                    MidtransSDK.getInstance().uiKitCustomSetting = uiKitCustomSetting
                    MidtransSDK.getInstance().startPaymentUiFlow(context, getMidtransToken.toString())
                }
            }

        }
    }

    private fun midtransInitialotation() {
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
            .setMerchantBaseUrl(BuildConfig.BASE_URL) //set merchant url (required)
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

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}