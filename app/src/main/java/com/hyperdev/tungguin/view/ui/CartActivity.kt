package com.hyperdev.tungguin.view.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.gson.Gson
import com.hyperdev.tungguin.BuildConfig
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.adapter.CartListAdapter
import com.hyperdev.tungguin.database.SharedPrefManager
import com.hyperdev.tungguin.model.cart.CartData
import com.hyperdev.tungguin.model.cart.Item
import com.hyperdev.tungguin.model.checkkupon.CheckKuponResponse
import com.hyperdev.tungguin.model.checkout.CheckoutResponse
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.network.NetworkUtil
import com.hyperdev.tungguin.presenter.CartPresenter
import com.hyperdev.tungguin.repository.CartRepositoryImpl
import com.hyperdev.tungguin.repository.DeleteCartRepositoryImpl
import com.hyperdev.tungguin.utils.AppSchedulerProvider
import com.hyperdev.tungguin.view.CartListView
import com.hyperdev.tungguin.view.CartView
import com.hyperdev.tungguin.view.DeleteCartView
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.core.UIKitCustomSetting
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme
import com.midtrans.sdk.corekit.models.snap.TransactionResult
import com.midtrans.sdk.uikit.SdkUIFlowBuilder
import kotlinx.android.synthetic.main.activity_cart.*
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import kotlin.properties.Delegates

class CartActivity : AppCompatActivity(), CartView.View, CartListView, DeleteCartView.View {

    //Deklarasi Variable
    private var listCartItem: MutableList<Item> = mutableListOf()
    private lateinit var presenter: CartView.Presenter
    private lateinit var token: String
    private lateinit var baseApiService: BaseApiService
    private var disabledAddCart: Boolean = false
    private var adapter by Delegates.notNull<CartListAdapter>()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setSupportActionBar(toolbar)

        check.setOnClickListener {
            if(kode_voucer.text.toString().isEmpty()){
                Toast.makeText(this@CartActivity, "Kode Voucher tidak boleh kosong!", Toast.LENGTH_SHORT).show()
            }else{
                refreshLayput.isRefreshing = true
                checkKupon("Bearer $token", kode_voucer.text.toString())
            }
        }

        refreshLayput.setOnRefreshListener {
            loadData()
        }

        btnPayment.setOnClickListener {
            val menuItem = arrayOf("Bayar dengan Tungguin Pay", "Metode Pembayaran Lain")
            val message: AlertDialog.Builder = AlertDialog.Builder(it.context)
                .setTitle("Pilih Metode Pembayaran")
                .setItems(menuItem) { _, which ->
                    when(which){
                        0 ->{
                            paymentResponse("wallet", kode_voucer.text.toString())
                        }

                        1 ->{
                            paymentResponse("midtrans", kode_voucer.text.toString())
                        }
                    }
                }

            message.create()
            message.show()
        }
    }

    private fun paymentResponse(paymentTipe: String, voucher: String){
        progressBar.visibility = View.VISIBLE
        shadow.visibility = View.VISIBLE

        baseApiService.checkout("Bearer $token", "application/json", paymentTipe, voucher)

            .enqueue(object : Callback<CheckoutResponse> {

                override fun onFailure(call: Call<CheckoutResponse>, t: Throwable) {
                    Log.e("RegisterPage.kt", "onFailure ERROR -> "+ t.message)
                    showSnackBarMessage(t.localizedMessage.toString())
                    progressBar.visibility = View.GONE
                    shadow.visibility = View.GONE
                }

                override fun onResponse(call: Call<CheckoutResponse>, response: Response<CheckoutResponse>) {

                    if (response.isSuccessful) {
                        SharedPrefManager.getInstance(this@CartActivity).deleteDesigner()
                        Log.i("debug", "onResponse: BERHASIL")
                        try {
                            progressBar.visibility = View.GONE
                            shadow.visibility = View.GONE
                            val hashedId = response.body()?.checkoutItem?.hashedId.toString()
                            midtransInitialotation(hashedId)
                            if(paymentTipe == "wallet"){
                                Toast.makeText(this@CartActivity, "Pembayaran Berhasil", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@CartActivity, SearchDesignerActivity::class.java)
                                intent.putExtra("sendOrderID", hashedId)
                                startActivity(intent)
                            }else if(paymentTipe == "midtrans"){

                                // Skip Customer Detail
                                val uiKitCustomSetting: UIKitCustomSetting = MidtransSDK.getInstance().uiKitCustomSetting
                                uiKitCustomSetting.isSkipCustomerDetailsPages = true
                                MidtransSDK.getInstance().uiKitCustomSetting = uiKitCustomSetting

                                // Start Payment
                                val tokenMidtrans = response.body()?.checkoutItem?.midtransToken.toString()
                                MidtransSDK.getInstance().startPaymentUiFlow(this@CartActivity, tokenMidtrans)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                    }else{
                        progressBar.visibility = View.GONE
                        shadow.visibility = View.GONE
                        val gson = Gson()
                        val message = gson.fromJson(response.errorBody()?.charStream(), CheckoutResponse::class.java)
                        if(message != null){
                            Toast.makeText(this@CartActivity, message.meta?.message.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })
    }

    private fun midtransInitialotation(hashedId: String){
        SdkUIFlowBuilder.init()
            .setClientKey(BuildConfig.MIDTRANS_CLIENTID)
            .setContext(this@CartActivity)
            .setTransactionFinishedCallback {
                if (it.response != null) {
                    when (it.status) {
                        TransactionResult.STATUS_SUCCESS -> {
                            Toast.makeText(
                                this@CartActivity,
                                "Transaction Finished ",
                                Toast.LENGTH_LONG
                            ).show()
                            val intent = Intent(this@CartActivity, SearchDesignerActivity::class.java)
                            intent.putExtra("sendOrderID", hashedId)
                            startActivity(intent)
                        }
                        TransactionResult.STATUS_PENDING -> {
                            val intent = Intent(this@CartActivity, SearchDesignerActivity::class.java)
                            intent.putExtra("sendOrderID", hashedId)
                            startActivity(intent)
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
                        Toast.makeText(this@CartActivity, "Transaction Finished with failure.", Toast.LENGTH_LONG).show()
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

    private fun checkKupon(token: String, code: String){
        baseApiService.checkKupon(token, "application/json", code)
            .enqueue(object : Callback<CheckKuponResponse> {

                override fun onFailure(call: Call<CheckKuponResponse>, t: Throwable) {
                    showSnackBarMessage("Tidak terhubung dengan internet !")
                    refreshLayput.isRefreshing = false
                }

                override fun onResponse(call: Call<CheckKuponResponse>, response: Response<CheckKuponResponse>) {

                    if (response.isSuccessful) {
                        Log.i("debug", "onResponse: BERHASIL")
                        try {
                            val condition: Boolean? = response.body()?.kuponData?.fisAvailable

                            if (condition!!){
                                Toast.makeText(this@CartActivity, "Voucher Tersedia", Toast.LENGTH_LONG).show()
                                kode_voucer.setTextColor(Color.parseColor("#00ce5c"))
                            }else{
                                Toast.makeText(this@CartActivity, "Voucher Tidak Tersedia", Toast.LENGTH_LONG).show()
                                kode_voucer.setTextColor(Color.parseColor("#d40101"))
                            }

                            refreshLayput.isRefreshing = false
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        Log.i("debug", "onResponse: GA BERHASIL")
                        refreshLayput.isRefreshing = false
                        val gson = Gson()
                        val message = gson.fromJson(response.errorBody()?.charStream(), CheckKuponResponse::class.java)
                        if(message != null){
                            Toast.makeText(this@CartActivity, message.meta?.message.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })
    }

    private fun loadData(){
        token = SharedPrefManager.getInstance(this@CartActivity).token.toString()

        baseApiService = NetworkUtil.getClient(this@CartActivity)!!
            .create(BaseApiService::class.java)

        val layout = LinearLayoutManager(this@CartActivity)
        orderItem_view.layoutManager = layout
        orderItem_view.setHasFixedSize(true)

        val request = CartRepositoryImpl(baseApiService)
        val request2 = DeleteCartRepositoryImpl(baseApiService)
        val scheduler = AppSchedulerProvider()
        presenter = CartPresenter(this, this@CartActivity, request, scheduler)

        adapter = CartListAdapter(listCartItem as ArrayList<Item>, this@CartActivity,
            this, request2, scheduler, "Bearer $token")

        presenter.getCartData("Bearer $token")

        orderItem_view.adapter = adapter
    }

    @SuppressLint("SetTextI18n")
    override fun shaowItemCount(count: String) {
        text_jumlah_produk.text = "Jumlah ($count produk)"
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if(!disabledAddCart){
            menuInflater.inflate(R.menu.cart_menu, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.addOrder -> {
                startActivity(Intent(this@CartActivity, OrderWithSliderActivity::class.java))
            }
        }
        return true
    }

    override fun onSuccess() {
        presenter.getCartData("Bearer $token")
    }

    override fun showCartItem(cartItem: List<Item>) {
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

    override fun displayProgress() {
        refreshLayput.isRefreshing = true
        item_cart_layout.visibility = View.GONE
    }

    override fun hideProgress() {
        refreshLayput.isRefreshing = false
        item_cart_layout.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun showSnackBarMessage(message: String) {
        Snackbar.make(keranjang_layout, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun displayProgressDelete() {
        refreshLayput.isRefreshing = true
    }

    override fun hideProgressDelete() {
        refreshLayput.isRefreshing = false
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }
}
