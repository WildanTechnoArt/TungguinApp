package com.hyperdev.tungguin.ui.activity

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.RatingBar
import androidx.appcompat.app.AppCompatActivity
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.database.SharedPrefManager
import com.hyperdev.tungguin.model.detailorder.DetailOrderData
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.network.ConnectivityStatus
import com.hyperdev.tungguin.network.HandleError
import com.hyperdev.tungguin.network.NetworkClient
import com.hyperdev.tungguin.presenter.ReviewOrderPresenter
import com.hyperdev.tungguin.ui.view.ReviewOrderView
import com.hyperdev.tungguin.utils.AppSchedulerProvider
import com.hyperdev.tungguin.utils.UtilsConstant.Companion.HASHED_ID
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.android.synthetic.main.activity_testimoni.*
import retrofit2.HttpException
import java.net.SocketTimeoutException

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

        orderId = intent?.getStringExtra(HASHED_ID).toString()
        token = SharedPrefManager.getInstance(this).token.toString()

        baseApiService = NetworkClient.getClient(this)
            .create(BaseApiService::class.java)

        val scheduler = AppSchedulerProvider()
        presenter = ReviewOrderPresenter(this, baseApiService, scheduler)
        presenter.getDetailOrder("Bearer $token", orderId)

        btn_send.setOnClickListener {
            sendReview()
        }

        rating_bar.onRatingBarChangeListener = RatingBar
            .OnRatingBarChangeListener { _, rating, _ -> starCount = rating }

        input_amount!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            @SuppressLint("SetTextI18n")
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    customAmount = input_amount.text.toString()
                    tipPrice = customAmount.toInt()
                } catch (ex: NumberFormatException) {
                    ex.printStackTrace()
                }
            }
        })

        initButtonTip()

    }

    private fun sendReview() {

        designerTesti = input_testimoni_designer.text.toString()
        appTesti = input_testimoni_app.text.toString()

        presenter.sendTestimoni(
            "Bearer $token", "application/json", orderId, starCount.toString(),
            designerTesti, appTesti, tipPrice.toString()
        )

    }

    private fun initButtonTip() {

        btn_tipsatu.setOnClickListener {
            tipButton(tip1 = true, tip2 = false, tip3 = false, tip4 = false, tip5 = false, tip6 = false)
            tipPrice = 1000
            input_amount.setText(tipPrice.toString())
        }

        btn_tipdua.setOnClickListener {
            tipButton(tip1 = false, tip2 = true, tip3 = false, tip4 = false, tip5 = false, tip6 = false)
            tipPrice = 2500
            input_amount.setText(tipPrice.toString())
        }

        btn_tiptiga.setOnClickListener {
            tipButton(tip1 = false, tip2 = false, tip3 = true, tip4 = false, tip5 = false, tip6 = false)
            tipPrice = 4000
            input_amount.setText(tipPrice.toString())
        }

        btn_tipempat.setOnClickListener {
            tipButton(tip1 = false, tip2 = false, tip3 = false, tip4 = true, tip5 = false, tip6 = false)
            tipPrice = 6000
            input_amount.setText(tipPrice.toString())
        }

        btn_tiplima.setOnClickListener {
            tipButton(tip1 = false, tip2 = false, tip3 = false, tip4 = false, tip5 = true, tip6 = false)
            tipPrice = 7500
            input_amount.setText(tipPrice.toString())
        }

        btn_tipenam.setOnClickListener {
            tipButton(tip1 = false, tip2 = false, tip3 = false, tip4 = false, tip5 = false, tip6 = true)
            tipPrice = 10000
            input_amount.setText(tipPrice.toString())
        }
    }

    private fun tipButton(
        tip1: Boolean, tip2: Boolean, tip3: Boolean,
        tip4: Boolean, tip5: Boolean, tip6: Boolean
    ) {
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
    override fun showDetailOrder(data: DetailOrderData?) {
        tv_designer_name.text = data?.designer?.name.toString()
        kode_designer.text = "Kode: ${data?.designer?.formattedId.toString()}"
        date_order.text = data?.formattedDate.toString()
        order_id.text = data?.formattedId.toString()
    }

    override fun displayProgress() {
        progress_bar.visibility = View.VISIBLE
        shadow.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progress_bar.visibility = View.GONE
        shadow.visibility = View.GONE
    }

    override fun onSuccess() {
        progress_bar.visibility = View.GONE
        shadow.visibility = View.GONE
    }


    override fun onSuccessSend() {
        FancyToast.makeText(
            this,
            "Terimakasih atas ulasan Anda",
            FancyToast.LENGTH_SHORT,
            FancyToast.INFO,
            false
        ).show()
        finish()
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
}
