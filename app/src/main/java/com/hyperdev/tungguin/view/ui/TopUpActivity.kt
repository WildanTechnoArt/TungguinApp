package com.hyperdev.tungguin.view.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.hyperdev.tungguin.BuildConfig
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.database.SharedPrefManager
import com.hyperdev.tungguin.model.login.LoginResponse
import com.hyperdev.tungguin.model.profile.DataUser
import com.hyperdev.tungguin.model.topup.TopUpResponse
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.network.NetworkUtil
import com.hyperdev.tungguin.presenter.TopUpPresenter
import com.hyperdev.tungguin.repository.ProfileRepositoryImpl
import com.hyperdev.tungguin.utils.AppSchedulerProvider
import com.hyperdev.tungguin.view.TopUpView
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.core.TransactionRequest
import kotlinx.android.synthetic.main.activity_top_up.*
import java.text.NumberFormat
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme
import com.midtrans.sdk.corekit.models.snap.TransactionResult
import com.midtrans.sdk.uikit.SdkUIFlowBuilder
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class TopUpActivity : AppCompatActivity(), TopUpView.View {

    //Deklarasi Variable
    private lateinit var baseApiService: BaseApiService
    private var topupAmount: Long = 0
    private lateinit var customAmount: String
    private lateinit var getToken: String
    private lateinit var topupPresenter: TopUpView.Presenter
    private lateinit var moneyFormat: NumberFormat

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_top_up)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        //Inisialisasi Toolbar dan Menampilkan Menu Home pada Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        swipeRefresh.setOnRefreshListener {
            topupPresenter.getUserAmount(this@TopUpActivity, "Bearer $getToken")
        }

        midtransInitialotation()

        inputAmount!!.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            @SuppressLint("SetTextI18n")
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                try{
                    customAmount = inputAmount.text.toString()
                    topupAmount = customAmount.toLong()
                    totalTopUp.text = moneyFormat.format(topupAmount)
                }catch (ex: NumberFormatException){
                    ex.printStackTrace()
                }
            }
        })

        topupButton()

        btnTopUp.setOnClickListener {
            topupWallet()
        }
    }

    private fun loadData(){

        val connectivity  = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivity.activeNetworkInfo

        if (networkInfo != null && networkInfo.isConnected) {
            baseApiService = NetworkUtil.getClient()!!
                .create(BaseApiService::class.java)

            getToken = SharedPrefManager.getInstance(this@TopUpActivity).token.toString()
            moneyFormat = NumberFormat.getCurrencyInstance()

            val request = ProfileRepositoryImpl(baseApiService)
            val scheduler = AppSchedulerProvider()
            topupPresenter = TopUpPresenter(this, request, scheduler)
            topupPresenter.getUserAmount(this@TopUpActivity, "Bearer $getToken")
        } else {
            swipeRefresh.isRefreshing = false
            Snackbar.make(topupLayout, "Tidak terhubung dengan internet !", Snackbar.LENGTH_LONG).show()
        }
    }

    private fun topupWallet(){
        try{
            swipeRefresh.isRefreshing = true
            baseApiService.topUpMoney("Bearer $getToken", topupAmount.toString())
                .enqueue(object : Callback<TopUpResponse> {

                    override fun onFailure(call: Call<TopUpResponse>, t: Throwable) {
                        Log.e("TopUpActivity.kt", "onFailure ERROR -> "+ t.message)
                        showSnackBarMessage("Tidak terhubung dengan internet !")
                        swipeRefresh.isRefreshing = false
                    }

                    override fun onResponse(call: Call<TopUpResponse>, response: Response<TopUpResponse>) {

                        if (response.isSuccessful) {
                            Log.i("debug", "onResponse: BERHASIL")
                            try {
                                val midtransToken: String = response.body()?.getData?.getMitransToken.toString()
                                val transId: String = response.body()?.getData?.getFormattedId.toString()
                                val message = response.body()?.getMeta?.message
                                if(message != null){
                                    Toast.makeText(this@TopUpActivity, message.toString(), Toast.LENGTH_SHORT).show()
                                }
                                transactionRequest(transId, topupAmount)
                                MidtransSDK.getInstance().startPaymentUiFlow(this@TopUpActivity, midtransToken)
                                swipeRefresh.isRefreshing = false
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        } else {
                            Log.i("debug", "onResponse: GA BERHASIL")
                            val gson = Gson()
                            val message = gson.fromJson(response.errorBody()?.charStream(), LoginResponse::class.java)
                            if(message != null){
                                Toast.makeText(this@TopUpActivity, message.getMeta?.message.toString(), Toast.LENGTH_SHORT).show()
                            }
                            swipeRefresh.isRefreshing = false
                        }
                    }
                })
        }catch(ex: Exception){
            ex.printStackTrace()
            swipeRefresh.isRefreshing = false
            Toast.makeText(this@TopUpActivity, "Gagal membuat data, Silakan coba lagi !", Toast.LENGTH_SHORT).show()

        }
    }

    private fun transactionRequest(transactionID: String, topupAmount: Long){
        val transactionRequest = TransactionRequest(transactionID, topupAmount.toDouble())
        // Set transaction request into SDK instance
        MidtransSDK.getInstance().transactionRequest = transactionRequest
    }

    private fun midtransInitialotation(){
        SdkUIFlowBuilder.init()
            .setClientKey(BuildConfig.MIDTRANS_CLIENTID)
            .setContext(this@TopUpActivity)
            .setTransactionFinishedCallback {
                if (it.response != null) {
                    when (it.status) {
                        TransactionResult.STATUS_SUCCESS -> {
                            Toast.makeText(
                                this@TopUpActivity,
                                "Transaction Finished ",
                                Toast.LENGTH_LONG
                            ).show()
                            finish()
                        }
                        TransactionResult.STATUS_PENDING -> (this@TopUpActivity as Activity).finish()
                        TransactionResult.STATUS_FAILED -> Toast.makeText(
                            this@TopUpActivity,
                            "Transaction Failed",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    it.response.validationMessages
                } else if (it.isTransactionCanceled) {
                    Toast.makeText(this@TopUpActivity, "Transaction Canceled", Toast.LENGTH_LONG).show()
                } else {
                    if (it.status.equals(TransactionResult.STATUS_INVALID, ignoreCase = true)) {
                        Toast.makeText(this@TopUpActivity, "Transaction Invalid", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this@TopUpActivity, "Transaction Finished with failure.", Toast.LENGTH_LONG).show()
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

    private fun topupButton(){
        topup25k.setOnClickListener {
            topupAmount = 25000
            inputAmount.setText(topupAmount.toString())
            totalTopUp.text = moneyFormat.format(topupAmount)
        }
        topup50k.setOnClickListener {
            topupAmount = 50000
            inputAmount.setText(topupAmount.toString())
            totalTopUp.text = moneyFormat.format(topupAmount)
        }
        topup75k.setOnClickListener {
            topupAmount = 75000
            inputAmount.setText(topupAmount.toString())
            totalTopUp.text = moneyFormat.format(topupAmount)
        }
        topup100k.setOnClickListener {
            topupAmount = 100000
            inputAmount.setText(topupAmount.toString())
            totalTopUp.text = moneyFormat.format(topupAmount)
        }
        topup200k.setOnClickListener {
            topupAmount = 200000
            inputAmount.setText(topupAmount.toString())
            totalTopUp.text = moneyFormat.format(topupAmount)
        }
        topup300k.setOnClickListener {
            topupAmount = 300000
            inputAmount.setText(topupAmount.toString())
            totalTopUp.text = moneyFormat.format(topupAmount)
        }
        topup500k.setOnClickListener {
            topupAmount = 500000
            inputAmount.setText(topupAmount.toString())
            totalTopUp.text = moneyFormat.format(topupAmount)
        }
        topup750k.setOnClickListener {
            topupAmount = 750000
            inputAmount.setText(topupAmount.toString())
            totalTopUp.text = moneyFormat.format(topupAmount)
        }
        topup1000k.setOnClickListener {
            topupAmount = 1000000
            inputAmount.setText(topupAmount.toString())
            totalTopUp.text = moneyFormat.format(topupAmount)
        }
    }

    override fun onResume() {
        super.onResume()
        swipeRefresh.isRefreshing = true
        loadData()
    }

    override fun displayAmount(profileItem: DataUser) {
        user_balance.text = profileItem.formattedBalance.toString()
    }

    override fun displayProgress() {
        swipeRefresh.isRefreshing = true
    }

    override fun hideProgress() {
        swipeRefresh.isRefreshing = false
    }

    private fun showSnackBarMessage(message: String) {
        Snackbar.make(topupLayout, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}