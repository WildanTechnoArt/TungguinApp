package com.hyperdev.tungguin.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.hyperdev.tungguin.BuildConfig
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.adapter.TopupListAdapter
import com.hyperdev.tungguin.database.SharedPrefManager
import com.hyperdev.tungguin.model.profile.DataUser
import com.hyperdev.tungguin.model.topup.DataTopUp
import com.hyperdev.tungguin.model.topup.TopUpItemData
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.network.ConnectivityStatus
import com.hyperdev.tungguin.network.HandleError
import com.hyperdev.tungguin.network.NetworkClient
import com.hyperdev.tungguin.presenter.TopUpPresenter
import com.hyperdev.tungguin.ui.TopupListener
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
import retrofit2.HttpException
import java.net.SocketTimeoutException
import kotlin.properties.Delegates

class TopUpActivity : AppCompatActivity(), TopUpView.View, TopupListener {

    //Deklarasi Variable
    private lateinit var baseApiService: BaseApiService
    private lateinit var presenter: TopUpView.Presenter
    private var topupAmount: Long? = 0
    private lateinit var getToken: String
    private lateinit var midtransToken: String
    private lateinit var transactionId: String
    private var adapter by Delegates.notNull<TopupListAdapter>()
    private var topupAmountList = arrayListOf<TopUpItemData>()

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

        btnTopUp.setOnClickListener {
            presenter.topUpMoney("Bearer $getToken", "application/json", topupAmount.toString())
        }

        midtransInitialotation()
    }

    private fun initData() {

        baseApiService = NetworkClient.getClient(this)
            .create(BaseApiService::class.java)

        getToken = SharedPrefManager.getInstance(this).token.toString()

        val scheduler = AppSchedulerProvider()
        presenter = TopUpPresenter(this, baseApiService, scheduler)
        presenter.getUserAmount("Bearer $getToken")
        presenter.topUpAmount("Bearer $getToken", "application/json")
    }

    private fun transactionRequest(transactionID: String, topupAmount: Long?) {
        val transactionRequest = topupAmount?.toDouble()?.let {
            TransactionRequest(transactionID,
                it
            )
        }

        val creditCardOptions = CreditCard()
        // Set to true if you want to save card to Snap
        creditCardOptions.isSaveCard = false
        // Set bank name when using MIGS channel
        creditCardOptions.bank = BankType.BCA
        // Set MIGS channel (ONLY for BCA, BRI and Maybank Acquiring bank)
        creditCardOptions.channel = CreditCard.MIGS
        // Set Credit Card Options
        transactionRequest?.creditCard = creditCardOptions

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

    override fun showTopupAmount(amount: List<TopUpItemData>) {
        topupAmountList.addAll(amount)
        adapter = TopupListAdapter(topupAmountList, this)
        val layout = GridLayoutManager(this, 3)
        rv_topup_amount.layoutManager = layout
        rv_topup_amount.setHasFixedSize(true)
        rv_topup_amount.adapter = adapter
    }

    override fun onClickListener(amount: Long?, label: String) {
        topupAmount = amount
        total_topup.text = label
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

    override fun handleError(e: Throwable) {
        if (ConnectivityStatus.isConnected(this)) {
            when (e) {
                is HttpException -> // non 200 error codes
                    HandleError.handleError(e, e.code(), this)
                is SocketTimeoutException -> // connection errors
                    FancyToast.makeText(
                        this,
                        "Connection Timeout!",
                        FancyToast.LENGTH_SHORT,
                        FancyToast.ERROR,
                        false
                    ).show()
            }
        } else {
            FancyToast.makeText(
                this,
                "Tidak Terhubung Dengan Internet!",
                FancyToast.LENGTH_SHORT,
                FancyToast.ERROR,
                false
            ).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}