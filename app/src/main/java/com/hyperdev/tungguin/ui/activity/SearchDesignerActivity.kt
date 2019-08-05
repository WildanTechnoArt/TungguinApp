package com.hyperdev.tungguin.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.hyperdev.tungguin.BuildConfig
import com.hyperdev.tungguin.GlideApp
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.database.SharedPrefManager
import com.hyperdev.tungguin.model.detailorder.DetailOrderData
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.network.ConnectivityStatus
import com.hyperdev.tungguin.network.HandleError
import com.hyperdev.tungguin.network.NetworkClient
import com.hyperdev.tungguin.presenter.SearchDesignPresenter
import com.hyperdev.tungguin.ui.view.SearchDesignerView
import com.hyperdev.tungguin.utils.AppSchedulerProvider
import com.hyperdev.tungguin.utils.UtilsConstant.Companion.HASHED_ID
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.android.synthetic.main.activity_search_designer.*
import kotlinx.android.synthetic.main.designer_found_layout.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.URISyntaxException

class SearchDesignerActivity : AppCompatActivity(), SearchDesignerView.View {

    // Deklarasi Variable
    private lateinit var orderId: String
    private lateinit var baseApiService: BaseApiService
    private lateinit var presenter: SearchDesignerView.Presenter
    private lateinit var token: String
    private lateinit var mCountdownTimer: CountDownTimer
    private var mCustomerTimer: Int = 0

    // WebSocket
    private var socket: Socket? = null

    private fun initSocket() {
        try {
            socket = IO.socket(BuildConfig.WEBSOCKET_URL)
        } catch (ex: URISyntaxException) {
            ex.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_designer)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        orderId = intent?.getStringExtra(HASHED_ID).toString()
        token = SharedPrefManager.getInstance(this).token.toString()

        baseApiService = NetworkClient.getClient(this)
            .create(BaseApiService::class.java)

        val scheduler = AppSchedulerProvider()
        presenter = SearchDesignPresenter(this, baseApiService, scheduler)

        // Inisialisasi WebSocket
        initSocket()
        initDataSocket()

        presenter.getDetailOrder("Bearer $token", orderId)

        swipe_refresh.isEnabled = false

        btn_detail_order.setOnClickListener {
            val intent = Intent(this@SearchDesignerActivity, DetailOrderActivity::class.java)
            intent.putExtra(HASHED_ID, orderId)
            startActivity(intent)
            finish()
        }
    }

    private fun initDataSocket() {
        socket?.on("order", searchDesigner)
        socket?.connect()
        socket?.emit("order", orderId)
    }

    override fun onDestroy() {
        super.onDestroy()
        socket?.disconnect()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, DetailOrderActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra(HASHED_ID, orderId)
        startActivity(intent)
        finish()
    }

    @SuppressLint("SetTextI18n")
    private val searchDesigner = Emitter.Listener { args ->
        runOnUiThread {

            val data = args[0] as JSONObject
            val type: String?

            try {
                type = data.getString("type")
                if (type != null) {
                    when (type) {
                        "designerFound" -> {
                            FancyToast.makeText(
                                this,
                                "Designer ditemukan",
                                FancyToast.LENGTH_SHORT,
                                FancyToast.SUCCESS,
                                false
                            ).show()
                            presenter.getDetailOrder("Bearer $token", orderId)
                        }
                    }
                }
            } catch (ex: JSONException) {
                ex.printStackTrace()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun showDetailOrder(data: DetailOrderData?) {
        mCustomerTimer = data?.customerTimer ?: 0

        if ((data?.statusFormatted?.status.toString() == "searching_designer")) {
            mCountdownTimer = MyCountdownTimer()
            mCountdownTimer.start()

            progress_bar.visibility = View.VISIBLE
            tv_search_design.visibility = View.VISIBLE
            tv_search_design.text = data?.statusFormatted?.label.toString() + "...."

        } else if ((data?.statusFormatted?.status.toString() == "in_progress")) {

            designer_found_layour.visibility = View.VISIBLE

            progress_bar.visibility = View.GONE
            tv_search_design.visibility = View.GONE

            tv_designer_name.text = data?.designer?.name.toString()
            tv_id_designer.text = "Kode: ${data?.designer?.formattedId.toString()}"
            GlideApp.with(this@SearchDesignerActivity)
                .load(data?.designer?.photoUrl.toString())
                .placeholder(R.drawable.circle_profil)
                .into(img_profile_designer)

        }
    }

    override fun displayProgress() {
        swipe_refresh.isRefreshing = true
    }

    override fun hideProgress() {
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

    inner class MyCountdownTimer
        : CountDownTimer((mCustomerTimer * 1000).toLong(), 1000) {

        override fun onFinish() {
            val intent = Intent(this@SearchDesignerActivity, DetailOrderActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra(HASHED_ID, orderId)
            startActivity(intent)
            finish()
        }

        @SuppressLint("SetTextI18n")
        override fun onTick(millisUntilFinished: Long) {
            tv_timer.text = "${millisUntilFinished / 1000} Detik"
        }
    }
}