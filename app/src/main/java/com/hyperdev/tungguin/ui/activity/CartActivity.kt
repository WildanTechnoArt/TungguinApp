package com.hyperdev.tungguin.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.hyperdev.tungguin.BuildConfig
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.adapter.CartListAdapter
import com.hyperdev.tungguin.database.SharedPrefManager
import com.hyperdev.tungguin.model.cart.*
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.network.NetworkClient
import com.hyperdev.tungguin.presenter.CartPresenter
import com.hyperdev.tungguin.repository.cart.CartRepositoryImp
import com.hyperdev.tungguin.utils.AppSchedulerProvider
import com.hyperdev.tungguin.ui.view.MyCartView
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.core.UIKitCustomSetting
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme
import com.midtrans.sdk.corekit.models.snap.TransactionResult
import com.midtrans.sdk.uikit.SdkUIFlowBuilder
import kotlinx.android.synthetic.main.activity_cart.*
import kotlin.properties.Delegates

class CartActivity : AppCompatActivity(), MyCartView.View {

    //Deklarasi Variable
    private var listCartItem: MutableList<CartItem> = mutableListOf()
    private lateinit var presenter: MyCartView.Presenter
    private lateinit var token: String
    private lateinit var baseApiService: BaseApiService
    private var disabledAddCart: Boolean = false
    private var adapter by Delegates.notNull<CartListAdapter>()
    private var condition: Boolean? = null
    private lateinit var hashedId: String
    private lateinit var paymentMethod: String
    private lateinit var tokenMidtrans: String

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setSupportActionBar(toolbar)

        initData()

        check.setOnClickListener {
            if (kode_voucer.text.toString().isEmpty()) {
                Toast.makeText(this@CartActivity, "Kode Voucher tidak boleh kosong!", Toast.LENGTH_SHORT).show()
            } else {
                presenter.checkVoucher("Bearer $token", "application/json", kode_voucer.text.toString())
            }
        }

        btnPayment.setOnClickListener {
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
                                kode_voucer.text.toString()
                            )
                        }

                        1 -> {
                            paymentMethod = "midtrans"
                            presenter.checkout(
                                "Bearer $token",
                                "application/json",
                                paymentMethod,
                                kode_voucer.text.toString()
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
            .setContext(this@CartActivity)
            .setTransactionFinishedCallback {
                if (it.response != null) {
                    when (it.status) {
                        TransactionResult.STATUS_SUCCESS -> {
                            Toast.makeText(
                                this@CartActivity,
                                "Transaction Finished",
                                Toast.LENGTH_LONG
                            ).show()
                            val intent = Intent(this@CartActivity, SearchDesignerActivity::class.java)
                            intent.putExtra("sendOrderID", hashedId)
                            startActivity(intent)
                            finish()
                        }
                        TransactionResult.STATUS_PENDING -> {
                            val intent = Intent(this@CartActivity, DetailOrderActivity::class.java)
                            intent.putExtra("sendOrderID", hashedId)
                            startActivity(intent)
                            finish()
                        }
                        TransactionResult.STATUS_FAILED -> Toast.makeText(
                            this@CartActivity,
                            "Transaction Failed",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    it.response.validationMessages
                } else if (it.isTransactionCanceled) {
                    Toast.makeText(this@CartActivity, "Transaction Canceled", Toast.LENGTH_LONG).show()
                } else {
                    if (it.status.equals(TransactionResult.STATUS_INVALID, ignoreCase = true)) {
                        Toast.makeText(this@CartActivity, "Transaction Invalid", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this@CartActivity, "Transaction Finished with failure.", Toast.LENGTH_LONG)
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

    private fun initData() {

        swipe_refresh.isEnabled = false

        token = SharedPrefManager.getInstance(this@CartActivity).token.toString()

        baseApiService = NetworkClient.getClient(this@CartActivity)!!
            .create(BaseApiService::class.java)

        val layout = LinearLayoutManager(this@CartActivity)
        orderItem_view.layoutManager = layout
        orderItem_view.setHasFixedSize(true)

        val repository = CartRepositoryImp(baseApiService)
        val scheduler = AppSchedulerProvider()

        presenter = CartPresenter(this, this@CartActivity, repository, scheduler)

        adapter = CartListAdapter(
            listCartItem as ArrayList<CartItem>, this@CartActivity, repository, scheduler, "Bearer $token"
        )

        orderItem_view.adapter = adapter
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
                startActivity(Intent(this@CartActivity, OrderActivity::class.java))
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
        jumlah_price.text = cartData.subTotal.toString()
        service_price.text = cartData.serviceFee.toString()
        total_price.text = cartData.total.toString()
        disabledAddCart = cartData.disableAddToCart!!
    }

    override fun noInternetConnection(message: String) {
        Snackbar.make(keranjang_layout, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onSuccessCheckVoucher() {
        if (condition!!) {
            Toast.makeText(this@CartActivity, "Voucher Tersedia", Toast.LENGTH_LONG).show()
            kode_voucer.setTextColor(Color.parseColor("#00ce5c"))
        } else {
            Toast.makeText(this@CartActivity, "Voucher Tidak Tersedia", Toast.LENGTH_LONG).show()
            kode_voucer.setTextColor(Color.parseColor("#d40101"))
        }

        swipe_refresh.isRefreshing = false
    }

    override fun onSuccessCheckout() {
        midtransInitialitation(hashedId)
        swipe_refresh.isRefreshing = false
        if (paymentMethod == "wallet") {
            Toast.makeText(this@CartActivity, "Pembayaran Berhasil", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@CartActivity, SearchDesignerActivity::class.java)
            intent.putExtra("sendOrderID", hashedId)
            startActivity(intent)
            finishAffinity()
        } else if (paymentMethod == "midtrans") {

            // Skip Customer Detail
            val uiKitCustomSetting: UIKitCustomSetting = MidtransSDK.getInstance().uiKitCustomSetting
            uiKitCustomSetting.isSkipCustomerDetailsPages = true
            MidtransSDK.getInstance().uiKitCustomSetting = uiKitCustomSetting

            // Start Payment
            MidtransSDK.getInstance().startPaymentUiFlow(this@CartActivity, tokenMidtrans)
        }
    }

    override fun showProgressBar() {
        swipe_refresh.isRefreshing = true
    }

    override fun hideProgressBar() {
        swipe_refresh.isRefreshing = false
    }

    override fun getVoucherData(data: DataVoucher) {
        condition = data.fisAvailable
    }

    override fun getCheckoutData(data: CheckoutData) {
        hashedId = data.hashedId.toString()
        tokenMidtrans = data.midtransToken.toString()
    }

    @SuppressLint("SetTextI18n")
    override fun showItemCount(count: String) {
        text_jumlah_produk.text = "Jumlah ($count produk)"
    }

    override fun onSuccessLoadData() {
        swipe_refresh.isRefreshing = false
    }

    override fun onSuccessDeleteItem() {
        presenter.getCartData("Bearer $token")
    }
}
