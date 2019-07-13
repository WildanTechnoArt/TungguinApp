package com.hyperdev.tungguin.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import com.hyperdev.tungguin.BuildConfig
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.database.SharedPrefManager
import com.hyperdev.tungguin.model.profile.DataUser
import com.hyperdev.tungguin.model.transaction.DataTopUp
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.network.NetworkClient
import com.hyperdev.tungguin.presenter.TopUpPresenter
import com.hyperdev.tungguin.repository.profile.ProfileRepositoryImpl
import com.hyperdev.tungguin.repository.transaction.TransactionRepositoryImp
import com.hyperdev.tungguin.utils.AppSchedulerProvider
import com.hyperdev.tungguin.ui.view.TopUpView
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.core.TransactionRequest
import com.midtrans.sdk.corekit.core.UIKitCustomSetting
import kotlinx.android.synthetic.main.activity_top_up.*
import java.text.NumberFormat
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme
import com.midtrans.sdk.corekit.models.snap.TransactionResult
import com.midtrans.sdk.uikit.SdkUIFlowBuilder

class TopUpActivity : AppCompatActivity(), TopUpView.View {

    //Deklarasi Variable
    private lateinit var baseApiService: BaseApiService
    private lateinit var presenter: TopUpView.Presenter
    private var topupAmount: Long = 0
    private lateinit var customAmount: String
    private lateinit var getToken: String
    private lateinit var moneyFormat: NumberFormat
    private lateinit var midtransToken: String
    private lateinit var transactionId: String

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

        initData()

        swipeRefresh.setOnRefreshListener {
            presenter.getUserAmount("Bearer $getToken")
        }

        midtransInitialotation()

        inputAmount!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            @SuppressLint("SetTextI18n")
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    customAmount = inputAmount.text.toString()
                    topupAmount = customAmount.toLong()
                    totalTopUp.text = moneyFormat.format(topupAmount)
                } catch (ex: NumberFormatException) {
                    ex.printStackTrace()
                }
            }
        })

        topupButton()

        btnTopUp.setOnClickListener {
            presenter.topUpMoney("Bearer $getToken", "application/json", topupAmount.toString())
        }
    }

    private fun initData() {

        baseApiService = NetworkClient.getClient(this@TopUpActivity)!!
            .create(BaseApiService::class.java)

        getToken = SharedPrefManager.getInstance(this@TopUpActivity).token.toString()
        moneyFormat = NumberFormat.getCurrencyInstance()

        val repository = ProfileRepositoryImpl(baseApiService)
        val repository2 = TransactionRepositoryImp(baseApiService)
        val scheduler = AppSchedulerProvider()
        presenter = TopUpPresenter(this, this@TopUpActivity, repository, repository2, scheduler)
        presenter.getUserAmount("Bearer $getToken")

    }

    private fun transactionRequest(transactionID: String, topupAmount: Long) {
        val transactionRequest = TransactionRequest(transactionID, topupAmount.toDouble())
        // Set transaction request into SDK instance
        MidtransSDK.getInstance().transactionRequest = transactionRequest
    }

    private fun midtransInitialotation() {
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
                        TransactionResult.STATUS_PENDING -> {
                            startActivity(Intent(this, HistoryActivity::class.java))
                            finish()
                        }
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
                        Toast.makeText(this@TopUpActivity, "Transaction Finished with failure.", Toast.LENGTH_LONG)
                            .show()
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

    private fun topupButton() {
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

    override fun displayAmount(profileItem: DataUser) {
        user_balance.text = profileItem.formattedBalance.toString()
    }

    override fun transactionData(data: DataTopUp) {
        midtransToken = data.getMitransToken.toString()
        transactionId = data.getFormattedId.toString()
    }

    override fun showProgressBar() {
        swipeRefresh.isRefreshing = true
    }

    override fun hideProgressBar() {
        swipeRefresh.isRefreshing = false
    }

    override fun onSuccess() {
        // Skip Customer Detail
        val uiKitCustomSetting: UIKitCustomSetting = MidtransSDK.getInstance().uiKitCustomSetting
        uiKitCustomSetting.isSkipCustomerDetailsPages = true
        MidtransSDK.getInstance().uiKitCustomSetting = uiKitCustomSetting

        // Start Payment
        transactionRequest(transactionId, topupAmount)
        MidtransSDK.getInstance().startPaymentUiFlow(this@TopUpActivity, midtransToken)
        swipeRefresh.isRefreshing = false
    }

    override fun noInternetConnection(message: String) {
        Snackbar.make(topupLayout, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}