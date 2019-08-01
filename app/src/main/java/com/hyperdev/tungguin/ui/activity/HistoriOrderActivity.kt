package com.hyperdev.tungguin.ui.activity

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.View
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.adapter.OrderHistoriAdapter
import com.hyperdev.tungguin.database.SharedPrefManager
import com.hyperdev.tungguin.model.historiorder.DataOrder
import com.hyperdev.tungguin.model.historiorder.OrderItem
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.network.ConnectivityStatus
import com.hyperdev.tungguin.network.HandleError
import com.hyperdev.tungguin.network.NetworkClient
import com.hyperdev.tungguin.presenter.OrderHistoryPresenter
import com.hyperdev.tungguin.utils.AppSchedulerProvider
import com.hyperdev.tungguin.ui.view.HistoryOrderView
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.android.synthetic.main.activity_history_order.*
import retrofit2.HttpException
import java.net.SocketTimeoutException
import kotlin.properties.Delegates

class HistoriOrderActivity : AppCompatActivity(), HistoryOrderView.View {

    //Deklarasi Variable
    private var listOrder: MutableList<OrderItem> = mutableListOf()
    private lateinit var presenter: HistoryOrderView.Presenter
    private lateinit var token: String
    private lateinit var baseApiService: BaseApiService
    private val TAG = javaClass.simpleName
    private var adapter by Delegates.notNull<OrderHistoriAdapter>()
    private var page = 1
    private lateinit var nextPageURL: String
    private var isLoading by Delegates.notNull<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_order)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        //Inisialisasi Toolbar dan Menampilkan Menu Home pada Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        loadData()

        rv_history_list.adapter = adapter

        initListener()

        swipe_refresh.setOnRefreshListener {
            loadData()
        }
    }

    private fun loadData() {
        token = SharedPrefManager.getInstance(this).token.toString()

        baseApiService = NetworkClient.getClient(this)
            .create(BaseApiService::class.java)

        val layout = LinearLayoutManager(this)
        rv_history_list.layoutManager = layout
        rv_history_list.setHasFixedSize(true)

        val scheduler = AppSchedulerProvider()
        presenter = OrderHistoryPresenter(this, baseApiService, scheduler)
        adapter = OrderHistoriAdapter(this, listOrder as ArrayList<OrderItem>)

        presenter.getOrderHistory("Bearer $token", page = page)
    }

    private fun initListener() {
        rv_history_list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                val countItem = linearLayoutManager.itemCount
                val lastVisiblePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition()
                val isLastPosition = countItem.minus(1) == lastVisiblePosition
                Log.d(TAG, "countItem: $countItem")
                Log.d(TAG, "lastVisiblePosition: $lastVisiblePosition")
                Log.d(TAG, "isLastPosition: $isLastPosition")
                if (!isLoading && isLastPosition && nextPageURL != "null") {
                    page = page.plus(1)
                    presenter.getOrderHistory("Bearer $token", page = page)
                }
            }
        })
    }

    override fun showOrderData(order: DataOrder) {
        nextPageURL = order.next_page_url.toString()
    }

    override fun showOrderHistory(data: List<OrderItem>) {

        if (page == 1) {
            listOrder.clear()
            listOrder.addAll(data)
            adapter.notifyDataSetChanged()
        } else {
            adapter.refreshAdapter(data)
        }

        if (adapter.itemCount != 0) {
            tv_img_not_found.visibility = View.GONE
            rv_history_list.visibility = View.VISIBLE
        } else {
            tv_img_not_found.visibility = View.VISIBLE
            rv_history_list.visibility = View.GONE
        }

    }

    override fun onSuccess() {
        swipe_refresh.isRefreshing = false
        isLoading = false
    }

    override fun displayProgress() {
        swipe_refresh.isRefreshing = true
        isLoading = true
    }

    override fun hideProgress() {
        swipe_refresh.isRefreshing = false
        isLoading = false
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this@HistoriOrderActivity, DashboardActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
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
