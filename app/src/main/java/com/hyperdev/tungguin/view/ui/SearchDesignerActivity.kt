package com.hyperdev.tungguin.view.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.Socket
import com.github.nkzawa.socketio.client.IO
import com.hyperdev.tungguin.GlideApp
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.database.SharedPrefManager
import com.hyperdev.tungguin.model.detailorder.OrderDetailItem
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.network.NetworkUtil
import com.hyperdev.tungguin.presenter.SearchDesignPresenter
import com.hyperdev.tungguin.repository.DetailOrderRepositoryImpl
import com.hyperdev.tungguin.utils.AppSchedulerProvider
import com.hyperdev.tungguin.view.SearchDesignerView
import kotlinx.android.synthetic.main.activity_search_designer.*
import org.json.JSONException
import org.json.JSONObject
import java.net.URISyntaxException

class SearchDesignerActivity : AppCompatActivity(), SearchDesignerView.View {

    // Deklarasi Variable
    private lateinit var orderId: String
    private lateinit var baseApiService: BaseApiService
    private lateinit var presenter: SearchDesignerView.Presenter
    private lateinit var token: String

    // WebSocket
    private var socket: Socket? = null

    private fun initSocket(){
        try{
            socket = IO.socket("https://tungguin-socket.azishapidin.com/")
        }catch (ex: URISyntaxException){
            ex.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_designer)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        orderId = intent.getStringExtra("sendOrderID").toString()
        token = SharedPrefManager.getInstance(this@SearchDesignerActivity).token.toString()

        baseApiService = NetworkUtil.getClient(this@SearchDesignerActivity)!!
            .create(BaseApiService::class.java)

        val request = DetailOrderRepositoryImpl(baseApiService)
        val scheduler = AppSchedulerProvider()
        presenter = SearchDesignPresenter(this@SearchDesignerActivity, this, request, scheduler)

        // Inisialisasi WebSocket
        initSocket()
        initDataSocket()

        presenter.getDetailOrder("Bearer $token", orderId)

        refreshLayput.setOnRefreshListener {
            presenter.getDetailOrder("Bearer $token", orderId)
        }

        btnDetailOrder.setOnClickListener {
            val intent = Intent(this@SearchDesignerActivity, DetailOrderActivity::class.java)
            intent.putExtra("sendOrderID", orderId)
            startActivity(intent)
            finish()
        }
    }

    private fun initDataSocket(){
        socket?.on("order", searchDesigner)
        socket?.connect()
        socket?.emit("order", orderId)
    }

    override fun onDestroy() {
        super.onDestroy()
        socket?.disconnect()
    }

    @SuppressLint("SetTextI18n")
    private val searchDesigner = Emitter.Listener { args ->
        runOnUiThread {

            val data = args[0] as JSONObject
            val type: String?

            try{
                type = data.getString("type")
                if(type != null){
                    when(type){
                        "designerFound" ->{
                            presenter.getDetailOrder("Bearer $token", orderId)
                        }
                    }
                }
            }catch (ex: JSONException){
                ex.printStackTrace()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun showDetailOrder(data: OrderDetailItem?) {
        if((data?.statusFormatted?.status.toString() == "searching_designer")){
            txt_yeah.visibility = View.GONE
            found_desainer.visibility = View.GONE
            txt_intro.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
            txt_search_design.visibility = View.VISIBLE
            profile_image.visibility = View.GONE
            id_designer.visibility = View.GONE
            btnDetailOrder.visibility = View.GONE
            txt_search_design.text = data?.statusFormatted?.label.toString()
        }else if((data?.statusFormatted?.status.toString() == "in_progress")){
            txt_yeah.visibility = View.VISIBLE
            found_desainer.visibility = View.VISIBLE
            txt_intro.visibility = View.VISIBLE
            name_designer.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
            txt_search_design.visibility = View.GONE
            profile_image.visibility = View.VISIBLE
            id_designer.visibility = View.VISIBLE
            btnDetailOrder.visibility = View.VISIBLE

            name_designer.text = data?.designer?.name.toString()
            id_designer.text = "Kode: ${data?.designer?.formattedId.toString()}"
            GlideApp.with(this@SearchDesignerActivity)
                .load(data?.designer?.photoUrl.toString())
                .placeholder(R.drawable.circle_profil)
                .into(profile_image)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onSuccess() {
        txt_yeah.text = "YEAYY"
        found_desainer.text = "Kami telah menemukan designer untuk anda."
        txt_intro.text = "Designer kami akan segera menghubungi anda."
    }

    override fun displayProgress() {
        refreshLayput.isRefreshing = true
        designer_layout.visibility = View.GONE
    }

    override fun hideProgress() {
        refreshLayput.isRefreshing = false
        designer_layout.visibility = View.VISIBLE
    }
}