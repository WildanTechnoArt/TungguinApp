package com.hyperdev.tungguin.view.ui

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.RatingBar
import android.widget.Toast
import com.google.gson.Gson
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.database.SharedPrefManager
import com.hyperdev.tungguin.model.detailorder.DetailOrderResponse
import com.hyperdev.tungguin.model.detailorder.OrderDetailItem
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.network.NetworkUtil
import com.hyperdev.tungguin.presenter.ReviewOrderPresenter
import com.hyperdev.tungguin.repository.ReviewOrderRepositoryImpl
import com.hyperdev.tungguin.utils.AppSchedulerProvider
import com.hyperdev.tungguin.view.ReviewOrderView
import kotlinx.android.synthetic.main.activity_testimoni.*
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class TestimoniActivity : AppCompatActivity(), ReviewOrderView.View {

    // Deklarasi Variable
    private lateinit var orderId: String
    private lateinit var baseApiService: BaseApiService
    private lateinit var presenter: ReviewOrderView.Presenter
    private lateinit var token: String
    private lateinit var customAmount: String
    private lateinit var designerTesti: String
    private lateinit var appTesti: String
    private var starCount: Float = 0f
    private var tipPrice: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_testimoni)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        orderId = intent.getStringExtra("sendOrderID").toString()
        token = SharedPrefManager.getInstance(this@TestimoniActivity).token.toString()

        baseApiService = NetworkUtil.getClient(this@TestimoniActivity)!!
            .create(BaseApiService::class.java)

        val request = ReviewOrderRepositoryImpl(baseApiService)
        val scheduler = AppSchedulerProvider()
        presenter = ReviewOrderPresenter(this@TestimoniActivity, this, request, scheduler)
        presenter.getDetailOrder("Bearer $token", orderId)

        btn_send.setOnClickListener {
            sendReview()
        }

        rating.onRatingBarChangeListener = RatingBar
            .OnRatingBarChangeListener { _, rating, _ -> starCount = rating }

        inputAmount!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            @SuppressLint("SetTextI18n")
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                try{
                    customAmount = inputAmount.text.toString()
                    tipPrice = customAmount.toInt()
                }catch (ex: NumberFormatException){
                    ex.printStackTrace()
                }
            }
        })

        initButtonTip()

    }

    private fun sendReview(){

        designerTesti = testimoni_designer.text.toString()
        appTesti = testimoni_app.text.toString()

        showLoading()
        baseApiService.sendReview("Bearer $token", "application/json", orderId, starCount.toString(),
            designerTesti, appTesti, tipPrice.toString())
            .enqueue(object : Callback<DetailOrderResponse> {

                override fun onFailure(call: Call<DetailOrderResponse>, t: Throwable) {
                    Toast.makeText(this@TestimoniActivity, t.message.toString(), Toast.LENGTH_SHORT).show()
                    hideLoading()
                }

                override fun onResponse(call: Call<DetailOrderResponse>, response: Response<DetailOrderResponse>) {

                    if (response.isSuccessful) {
                        Log.i("debug", "onResponse: BERHASIL")
                        try {
                            Toast.makeText(this@TestimoniActivity, "Terimakasih atas ulasan Anda", Toast.LENGTH_SHORT).show()
                            hideLoading()
                            finish()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        Log.i("debug", "onResponse: GA BERHASIL")
                        val gson = Gson()
                        val message = gson.fromJson(response.errorBody()?.charStream(), DetailOrderResponse::class.java)
                        if(message != null){
                            Toast.makeText(this@TestimoniActivity, message.meta?.message.toString(), Toast.LENGTH_SHORT).show()
                        }
                        hideLoading()
                    }
                }
            })

    }

    private fun initButtonTip(){

        btn_tipsatu.setOnClickListener {
            tipButton(tip1 = true, tip2 = false, tip3 = false, tip4 = false, tip5 = false, tip6 = false)
            tipPrice = 1000
            inputAmount.setText(tipPrice.toString())
        }

        btn_tipdua.setOnClickListener {
            tipButton(tip1 = false, tip2 = true, tip3 = false, tip4 = false, tip5 = false, tip6 = false)
            tipPrice = 2500
            inputAmount.setText(tipPrice.toString())
        }

        btn_tiptiga.setOnClickListener {
            tipButton(tip1 = false, tip2 = false, tip3 = true, tip4 = false, tip5 = false, tip6 = false)
            tipPrice = 4000
            inputAmount.setText(tipPrice.toString())
        }

        btn_tipempat.setOnClickListener {
            tipButton(tip1 = false, tip2 = false, tip3 = false, tip4 = true, tip5 = false, tip6 = false)
            tipPrice = 6000
            inputAmount.setText(tipPrice.toString())
        }

        btn_tiplima.setOnClickListener {
            tipButton(tip1 = false, tip2 = false, tip3 = false, tip4 = false, tip5 = true, tip6 = false)
            tipPrice = 7500
            inputAmount.setText(tipPrice.toString())
        }

        btn_tipenam.setOnClickListener {
            tipButton(tip1 = false, tip2 = false, tip3 = false, tip4 = false, tip5 = false, tip6 = true)
            tipPrice = 10000
            inputAmount.setText(tipPrice.toString())
        }
    }

    private fun tipButton(tip1: Boolean, tip2: Boolean, tip3: Boolean,
                          tip4: Boolean, tip5: Boolean, tip6: Boolean){
        when {
            tip1 -> {
                btn_tipsatu.setTextColor(Color.parseColor("#FFFFFF"))
                btn_tipdua.setTextColor(Color.parseColor("#000000"))
                btn_tiptiga.setTextColor(Color.parseColor("#000000"))
                btn_tipempat.setTextColor(Color.parseColor("#000000"))
                btn_tiplima.setTextColor(Color.parseColor("#000000"))
                btn_tipenam.setTextColor(Color.parseColor("#000000"))
            }
            tip2 -> {
                btn_tipsatu.setTextColor(Color.parseColor("#000000"))
                btn_tipdua.setTextColor(Color.parseColor("#FFFFFF"))
                btn_tiptiga.setTextColor(Color.parseColor("#000000"))
                btn_tipempat.setTextColor(Color.parseColor("#000000"))
                btn_tiplima.setTextColor(Color.parseColor("#000000"))
                btn_tipenam.setTextColor(Color.parseColor("#000000"))
            }
            tip3 -> {
                btn_tipsatu.setTextColor(Color.parseColor("#000000"))
                btn_tipdua.setTextColor(Color.parseColor("#000000"))
                btn_tiptiga.setTextColor(Color.parseColor("#FFFFFF"))
                btn_tipempat.setTextColor(Color.parseColor("#000000"))
                btn_tiplima.setTextColor(Color.parseColor("#000000"))
                btn_tipenam.setTextColor(Color.parseColor("#000000"))
            }
            tip4 -> {
                btn_tipsatu.setTextColor(Color.parseColor("#000000"))
                btn_tipdua.setTextColor(Color.parseColor("#000000"))
                btn_tiptiga.setTextColor(Color.parseColor("#000000"))
                btn_tipempat.setTextColor(Color.parseColor("#FFFFFF"))
                btn_tiplima.setTextColor(Color.parseColor("#000000"))
                btn_tipenam.setTextColor(Color.parseColor("#000000"))
            }
            tip5 -> {
                btn_tipsatu.setTextColor(Color.parseColor("#000000"))
                btn_tipdua.setTextColor(Color.parseColor("#000000"))
                btn_tiptiga.setTextColor(Color.parseColor("#000000"))
                btn_tipempat.setTextColor(Color.parseColor("#000000"))
                btn_tiplima.setTextColor(Color.parseColor("#FFFFFF"))
                btn_tipenam.setTextColor(Color.parseColor("#000000"))
            }
            tip6 -> {
                btn_tipsatu.setTextColor(Color.parseColor("#000000"))
                btn_tipdua.setTextColor(Color.parseColor("#000000"))
                btn_tiptiga.setTextColor(Color.parseColor("#000000"))
                btn_tipempat.setTextColor(Color.parseColor("#000000"))
                btn_tiplima.setTextColor(Color.parseColor("#000000"))
                btn_tipenam.setTextColor(Color.parseColor("#FFFFFF"))
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun showDetailOrder(data: OrderDetailItem?) {
        name_designer.text = data?.designer?.name.toString()
        kode_designer.text = "Kode: ${data?.designer?.formattedId.toString()}"
        date_order.text = data?.formattedDate.toString()
        order_id.text = data?.formattedId.toString()
    }

    override fun displayProgress() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        scroll_view.visibility = View.GONE
        progressBar.visibility = View.GONE
    }

    override fun onSuccess() {
        scroll_view.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
    }

    private fun showLoading(){
        progressBar.visibility = View.VISIBLE
        shadow.visibility = View.VISIBLE
    }

    private fun hideLoading(){
        progressBar.visibility = View.GONE
        shadow.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }
}
