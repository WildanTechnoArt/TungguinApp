package com.hyperdev.tungguin.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.hyperdev.tungguin.BuildConfig
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.adapter.CartAdapter
import com.hyperdev.tungguin.database.SharedPrefManager
import com.hyperdev.tungguin.model.cart.CartData
import com.hyperdev.tungguin.model.cart.CartItem
import com.hyperdev.tungguin.model.cart.CheckoutData
import com.hyperdev.tungguin.model.cart.DataVoucher
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.network.ConnectivityStatus
import com.hyperdev.tungguin.network.HandleError
import com.hyperdev.tungguin.network.NetworkClient
import com.hyperdev.tungguin.presenter.CartPresenter
import com.hyperdev.tungguin.ui.view.MyCartView
import com.hyperdev.tungguin.utils.AppSchedulerProvider
import com.hyperdev.tungguin.utils.UtilsConstant.Companion.HASHED_ID
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.core.UIKitCustomSetting
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme
import com.midtrans.sdk.corekit.models.snap.TransactionResult
import com.midtrans.sdk.uikit.SdkUIFlowBuilder
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.cart_information.*
import retrofit2.HttpException
import java.net.SocketTimeoutException
import kotlin.properties.Delegates

class CartActivity : AppCompatActivity(), MyCartView.View {

    private var listCartItem: MutableList<CartItem> = mutableListOf()
    private lateinit var presenter: MyCartView.Presenter
    private lateinit var token: String
    private lateinit var baseApiService: BaseApiService
    private var disabledAddCart: Boolean = false
    private var adapter by Delegates.notNull<CartAdapter>()
    private var condition = false
    private lateinit var hashedId: String
    private lateinit var paymentMethod: String
    private lateinit var tokenMidtrans: String
    private lateinit var voucherMessage: String

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setSupportActionBar(toolbar)

        initData()

        btn_check_coupon.setOnClickListener {
            if (input_coupon.text.toString().isEmpty()) {
                FancyToast.makeText(
                    this,
                    "Kode Kupon tidak boleh kosong!",
                    FancyToast.LENGTH_SHORT,
                    FancyToast.INFO,
                    false
                ).show()
            } else {
                presenter.checkVoucher("Bearer $token", "application/json", input_coupon.text.toString())
            }
        }

        btn_payment.setOnClickListener {
            val menuItem = arrayOf("Bayar dengan Tungguin Pay", "Metode Pembayaran Lain")
            val message: AlertDialog.Builder = AlertDialog.Builder(it.context)
                .setTitle("Pilih Metode Pembayaran")
                .setItems(menuItem) { _, which ->
                    when (which) {
                        0 -> {
                            paymentMethod = "wallet"
                            presenter.checkout(
                                "Bearer $token",
                                "application/json",
                                paymentMethod,
                                input_coupon.text.toString()
                            )
                        }

                        1 -> {
                            paymentMethod = "midtrans"
                            presenter.checkout(
                                "Bearer $token",
                                "application/json",
                                paymentMethod,
                                input_coupon.text.toString()
                            )
                        }
                    }
                }

            message.create()
            message.show()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun midtransInitialitation(hashedId: String) {
        SdkUIFlowBuilder.init()
            .setClientKey(BuildConfig.MIDTRANS_CLIENTID)
            .setContext(this)
            .setTransactionFinishedCallback {
                if (it.response != null) {
                    when (it.status) {
                        TransactionResult.STATUS_SUCCESS -> {
                            FancyToast.makeText(
                                this,
                                "Transaction Finished",
                                FancyToast.LENGTH_SHORT,
                                FancyToast.SUCCESS,
                                false
                            ).show()
                            val intent = Intent(this, SearchDesignerActivity::class.java)
                            intent.putExtra(HASHED_ID, hashedId)
                            startActivity(intent)
                            finishAffinity()
                        }
                        TransactionResult.STATUS_PENDING -> {
                            val intent = Intent(this, DetailOrderActivity::class.java)
                            intent.putExtra(HASHED_ID, hashedId)
                            startActivity(intent)
                            finishAffinity()
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

    private fun initData() {

        swipe_refresh.isEnabled = false

        token = SharedPrefManager.getInstance(this).token.toString()

        baseApiService = NetworkClient.getClient(this)
            .create(BaseApiService::class.java)

        val layout = LinearLayoutManager(this)
        rv_order_Item.layoutManager = layout
        rv_order_Item.setHasFixedSize(true)

        val scheduler = AppSchedulerProvider()

        presenter = CartPresenter(this, baseApiService, scheduler)

        adapter = CartAdapter(
            listCartItem as ArrayList<CartItem>, this, baseApiService, scheduler, "Bearer $token"
        )

        rv_order_Item.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        presenter.getCartData("Bearer $token")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (!disabledAddCart) {
            menuInflater.inflate(R.menu.cart_menu, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.addOrder -> {
                startActivity(Intent(this, OrderActivity::class.java))
            }
        }
        return true
    }

    override fun showCartItem(cartItem: List<CartItem>) {
        listCartItem.clear()
        listCartItem.addAll(cartItem)
        adapter.notifyDataSetChanged()
    }

    @SuppressLint("SetTextI18n")
    override fun showCartData(cartData: CartData) {
        order_price.text = cartData.subTotal.toString()
        service_fees.text = cartData.serviceFee.toString()
        total_price.text = cartData.total.toString()
        disabledAddCart = cartData.disableAddToCart!!
    }

    override fun onSuccessCheckVoucher() {
        if (condition) {
            FancyToast.makeText(this, voucherMessage, FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show()
            input_coupon.setTextColor(Color.parseColor("#00ce5c"))
        } else {
            FancyToast.makeText(
                this,
                "Kode kupon yang anda masukan salah",
                FancyToast.LENGTH_LONG,
                FancyToast.WARNING,
                false
            ).show()
            input_coupon.setTextColor(Color.parseColor("#d40101"))
        }

        swipe_refresh.isRefreshing = false
    }

    override fun onSuccessCheckout() {
        midtransInitialitation(hashedId)
        swipe_refresh.isRefreshing = false
        if (paymentMethod == "wallet") {
            FancyToast.makeText(this, "Pembayaran Berhasil", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show()
            val intent = Intent(this, SearchDesignerActivity::class.java)
            intent.putExtra(HASHED_ID, hashedId)
            startActivity(intent)
            finishAffinity()
        } else if (paymentMethod == "midtrans") {

            // Skip Customer Detail
            val uiKitCustomSetting: UIKitCustomSetting = MidtransSDK.getInstance().uiKitCustomSetting
            uiKitCustomSetting.isSkipCustomerDetailsPages = true
            MidtransSDK.getInstance().uiKitCustomSetting = uiKitCustomSetting

            // Start Payment
            MidtransSDK.getInstance().startPaymentUiFlow(this, tokenMidtrans)
        }
    }

    override fun showProgressBar() {
        swipe_refresh.isRefreshing = true
    }

    override fun hideProgressBar() {
        swipe_refresh.isRefreshing = false
    }

    override fun getVoucherData(data: DataVoucher) {
        voucherMessage = data.message.toString()
        condition = data.isAvailable ?: false
    }

    override fun getCheckoutData(data: CheckoutData) {
        hashedId = data.hashedId.toString()
        tokenMidtrans = data.midtransToken.toString()
    }

    @SuppressLint("SetTextI18n")
    override fun showItemCount(count: String) {
        tv_total_product.text = "Jumlah ($count produk)"
    }

    override fun onSuccessLoadData() {
        swipe_refresh.isRefreshing = false
    }

    override fun onSuccessDeleteItem() {
        presenter.getCartData("Bearer $token")
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
}
