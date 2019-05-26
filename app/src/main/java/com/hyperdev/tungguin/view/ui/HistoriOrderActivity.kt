package com.hyperdev.tungguin.view.ui

import android.content.pm.ActivityInfo
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.adapter.OrderHistoriAdapter
import com.hyperdev.tungguin.database.SharedPrefManager
import com.hyperdev.tungguin.model.historiorder.DataOrder
import com.hyperdev.tungguin.model.historiorder.OrderItem
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.network.NetworkUtil
import com.hyperdev.tungguin.presenter.OrderHistoriPresenter
import com.hyperdev.tungguin.repository.OrderHIstoryRepositoryImpl
import com.hyperdev.tungguin.utils.AppSchedulerProvider
import com.hyperdev.tungguin.view.HistoriOrderView
import kotlinx.android.synthetic.main.activity_histori_order.*
import kotlin.properties.Delegates

class HistoriOrderActivity : AppCompatActivity(), HistoriOrderView.View {

    //Deklarasi Variable
    private var listOrder: MutableList<OrderItem> = mutableListOf()
    private lateinit var presenter: HistoriOrderView.Presenter
    private lateinit var token: String
    private lateinit var baseApiService: BaseApiService
    private val TAG = javaClass.simpleName
    private var adapter by Delegates.notNull<OrderHistoriAdapter>()
    private var page by Delegates.notNull<Int>()
    private lateinit var nextPageURL: String
    private var isLoading by Delegates.notNull<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_histori_order)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        //Inisialisasi Toolbar dan Menampilkan Menu Home pada Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        page = 1

        loadData()

        historiList.adapter = adapter

        initListener()

        refreshLayout.setOnRefreshListener {
            loadData()
        }
    }

    private fun loadData(){
        token = SharedPrefManager.getInstance(this@HistoriOrderActivity).token.toString()

        baseApiService = NetworkUtil.getClient(this@HistoriOrderActivity)!!
            .create(BaseApiService::class.java)

        val layout = LinearLayoutManager(this@HistoriOrderActivity)
        historiList.layoutManager = layout
        historiList.setHasFixedSize(true)

        val request = OrderHIstoryRepositoryImpl(baseApiService)
        val scheduler = AppSchedulerProvider()
        presenter = OrderHistoriPresenter(this, this@HistoriOrderActivity, request, scheduler)
        adapter = OrderHistoriAdapter(this@HistoriOrderActivity, listOrder as ArrayList<OrderItem>)

        presenter.getOrderHistory("Bearer $token", page = page)
    }

    private fun initListener() {
        historiList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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

        if(page == 1){
            listOrder.clear()
            listOrder.addAll(data)
            adapter.notifyDataSetChanged()
        }else{
            adapter.refreshAdapter(data)
        }

        if(adapter.itemCount.toString() != "0"){
            txt_img_not_found.visibility = View.GONE
            historiList.visibility = View.VISIBLE
        }else{
            txt_img_not_found.visibility = View.VISIBLE
            historiList.visibility = View.GONE
        }

    }

    override fun onSuccess() {
        refreshLayout.isRefreshing = false
        isLoading = false
    }

    override fun displayProgress() {
        refreshLayout.isRefreshing = true
        isLoading = true
    }

    override fun hideProgress() {
        refreshLayout.isRefreshing = false
        isLoading = false
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }
}
