package com.hyperdev.tungguin.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.Socket
import com.github.nkzawa.socketio.client.IO
import com.hyperdev.tungguin.BuildConfig
import com.hyperdev.tungguin.GlideApp
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.database.SharedPrefManager
import com.hyperdev.tungguin.model.detailorder.DetailOrderData
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.network.NetworkClient
import com.hyperdev.tungguin.presenter.SearchDesignPresenter
import com.hyperdev.tungguin.repository.order.OrderRepositoryImp
import com.hyperdev.tungguin.utils.AppSchedulerProvider
import com.hyperdev.tungguin.ui.view.SearchDesignerView
import kotlinx.android.synthetic.main.activity_search_designer.*
import kotlinx.android.synthetic.main.designer_found_layout.*
import org.json.JSONException
import org.json.JSONObject
import java.net.URISyntaxException

class SearchDesignerActivity : AppCompatActivity(), SearchDesignerView.View {

    // Deklarasi Variable
    private lateinit var orderId: String
    private lateinit var baseApiService: BaseApiService
    private lateinit var presenter: SearchDesignerView.Presenter
    private lateinit var token: String
    private lateinit var mCountdownTimer: CountDownTimer


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

        orderId = intent.getStringExtra("sendOrderID").toString()
        token = SharedPrefManager.getInstance(this@SearchDesignerActivity).token.toString()

        baseApiService = NetworkClient.getClient(this@SearchDesignerActivity)!!
            .create(BaseApiService::class.java)

        val request = OrderRepositoryImp(baseApiService)
        val scheduler = AppSchedulerProvider()
        presenter = SearchDesignPresenter(this@SearchDesignerActivity, this, request, scheduler)

        // Inisialisasi WebSocket
        initSocket()
        initDataSocket()

        presenter.getDetailOrder("Bearer $token", orderId)

        swipe_refresh.setOnRefreshListener {
            presenter.getDetailOrder("Bearer $token", orderId)
            mCountdownTimer.start()
        }

        btn_detail_order.setOnClickListener {
            val intent = Intent(this@SearchDesignerActivity, DetailOrderActivity::class.java)
            intent.putExtra("sendOrderID", orderId)
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
        mCountdownTimer.cancel()
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
                            Toast.makeText(this, "Designer ditemukan", Toast.LENGTH_LONG).show()
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

    inner class MyCountdownTimer
        : CountDownTimer(30 * 1000, 1000) {

        override fun onFinish() {
            presenter.getDetailOrder("Bearer $token", orderId)
            mCountdownTimer.start()
        }

        @SuppressLint("SetTextI18n")
        override fun onTick(millisUntilFinished: Long) {
            tv_timer.text = "${millisUntilFinished / 1000} Sec"
        }
    }
}