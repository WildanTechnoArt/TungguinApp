package com.hyperdev.tungguin.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.hyperdev.tungguin.BuildConfig
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.database.SharedPrefManager
import com.hyperdev.tungguin.model.profile.DataUser
import com.hyperdev.tungguin.model.transaction.DataTopUp
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.network.NetworkClient
import com.hyperdev.tungguin.presenter.TopUpPresenter
import com.hyperdev.tungguin.ui.view.TopUpView
import com.hyperdev.tungguin.utils.AppSchedulerProvider
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.core.TransactionRequest
import com.midtrans.sdk.corekit.core.UIKitCustomSetting
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme
import com.midtrans.sdk.corekit.models.BankType
import com.midtrans.sdk.corekit.models.snap.CreditCard
import com.midtrans.sdk.corekit.models.snap.TransactionResult
import com.midtrans.sdk.uikit.SdkUIFlowBuilder
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.android.synthetic.main.activity_top_up.*
import java.text.NumberFormat

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
        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        initData()

        swipe_refresh.setOnRefreshListener {
            presenter.getUserAmount("Bearer $getToken")
        }

        midtransInitialotation()

        input_amount!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            @SuppressLint("SetTextI18n")
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    customAmount = input_amount.text.toString()
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

        baseApiService = NetworkClient.getClient(this)!!
            .create(BaseApiService::class.java)

        getToken = SharedPrefManager.getInstance(this).token.toString()
        moneyFormat = NumberFormat.getCurrencyInstance()

        val scheduler = AppSchedulerProvider()
        presenter = TopUpPresenter(this, this, baseApiService, scheduler)
        presenter.getUserAmount("Bearer $getToken")

    }

    private fun transactionRequest(transactionID: String, topupAmount: Long) {
        val transactionRequest = TransactionRequest(transactionID, topupAmount.toDouble())

        val creditCardOptions = CreditCard()
        // Set to true if you want to save card to Snap
        creditCardOptions.isSaveCard = false
        // Set bank name when using MIGS channel
        creditCardOptions.bank = BankType.BCA
        // Set MIGS channel (ONLY for BCA, BRI and Maybank Acquiring bank)
        creditCardOptions.channel = CreditCard.MIGS
        // Set Credit Card Options
        transactionRequest.creditCard = creditCardOptions

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
                            FancyToast.makeText(
                                this,
                                "Transaction Finished ",
                                FancyToast.LENGTH_SHORT,
                                FancyToast.SUCCESS,
                                false
                            ).show()
                            finish()
                        }
                        TransactionResult.STATUS_PENDING -> {
                            startActivity(Intent(this, HistoryActivity::class.java))
                            finish()
                        }
                        TransactionResult.STATUS_FAILED -> FancyToast.makeText(
                            this,
                            "Transaction Failed",
                            FancyToast.LENGTH_SHORT,
                            FancyToast.ERROR,
                            false
                        ).show()
                    }
                    it.response.validationMessages
                } else if (it.isTransactionCanceled) {
                    FancyToast.makeText(this, "Transaction Canceled", FancyToast.LENGTH_SHORT, FancyToast.INFO, false)
                        .show()
                } else {
                    if (it.status.equals(TransactionResult.STATUS_INVALID, ignoreCase = true)) {
                        FancyToast.makeText(
                            this,
                            "Transaction Invalid",
                            FancyToast.LENGTH_SHORT,
                            FancyToast.ERROR,
                            false
                        ).show()
                    } else {
                        FancyToast.makeText(
                            this,
                            "Transaction Finished with failure",
                            FancyToast.LENGTH_SHORT,
                            FancyToast.WARNING,
                            false
                        ).show()
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
            input_amount.setText(topupAmount.toString())
            totalTopUp.text = moneyFormat.format(topupAmount)
        }
        topup50k.setOnClickListener {
            topupAmount = 50000
            input_amount.setText(topupAmount.toString())
            totalTopUp.text = moneyFormat.format(topupAmount)
        }
        topup75k.setOnClickListener {
            topupAmount = 75000
            input_amount.setText(topupAmount.toString())
            totalTopUp.text = moneyFormat.format(topupAmount)
        }
        topup100k.setOnClickListener {
            topupAmount = 100000
            input_amount.setText(topupAmount.toString())
            totalTopUp.text = moneyFormat.format(topupAmount)
        }
        topup200k.setOnClickListener {
            topupAmount = 200000
            input_amount.setText(topupAmount.toString())
            totalTopUp.text = moneyFormat.format(topupAmount)
        }
        topup300k.setOnClickListener {
            topupAmount = 300000
            input_amount.setText(topupAmount.toString())
            totalTopUp.text = moneyFormat.format(topupAmount)
        }
        topup500k.setOnClickListener {
            topupAmount = 500000
            input_amount.setText(topupAmount.toString())
            totalTopUp.text = moneyFormat.format(topupAmount)
        }
        topup750k.setOnClickListener {
            topupAmount = 750000
            input_amount.setText(topupAmount.toString())
            totalTopUp.text = moneyFormat.format(topupAmount)
        }
        topup1000k.setOnClickListener {
            topupAmount = 1000000
            input_amount.setText(topupAmount.toString())
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
        swipe_refresh.isRefreshing = true
    }

    override fun hideProgressBar() {
        swipe_refresh.isRefreshing = false
    }

    override fun onSuccess() {
        // Skip Customer Detail
        val uiKitCustomSetting: UIKitCustomSetting = MidtransSDK.getInstance().uiKitCustomSetting
        uiKitCustomSetting.isSkipCustomerDetailsPages = true
        MidtransSDK.getInstance().uiKitCustomSetting = uiKitCustomSetting

        // Start Payment
        transactionRequest(transactionId, topupAmount)
        MidtransSDK.getInstance().startPaymentUiFlow(this@TopUpActivity, midtransToken)
        swipe_refresh.isRefreshing = false
    }

    override fun noInternetConnection(message: String) {
        Snackbar.make(topupLayout, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}